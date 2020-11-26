package com.autentia.tutoriales.reservas.teatro.saga;

import com.autentia.tutoriales.reservas.teatro.command.cliente.AplicarDescuentosCommand;
import com.autentia.tutoriales.reservas.teatro.command.cliente.Cliente;
import com.autentia.tutoriales.reservas.teatro.command.cliente.Descuento;
import com.autentia.tutoriales.reservas.teatro.command.cliente.RegistrarEmailCommand;
import com.autentia.tutoriales.reservas.teatro.command.pago.AnularPagoCommand;
import com.autentia.tutoriales.reservas.teatro.command.pago.Concepto;
import com.autentia.tutoriales.reservas.teatro.command.pago.Pago;
import com.autentia.tutoriales.reservas.teatro.command.pago.ProponerPagoIdempotentCommand;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.command.representacion.LiberarButacasCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.AbandonarReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.CrearReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.PagarReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.event.cliente.DescuentosAplicadosEvent;
import com.autentia.tutoriales.reservas.teatro.event.pago.PagoAnuladoEvent;
import com.autentia.tutoriales.reservas.teatro.event.pago.PagoConfirmadoEvent;
import com.autentia.tutoriales.reservas.teatro.event.representacion.ButacasSeleccionadasEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaAbandonadaEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaCanceladaEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaConfirmadaEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaPagadaEvent;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.RepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.Closeable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class ReservaTeatroSaga implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(ReservaTeatroSaga.class);

    private static final int TIMEOUT_RESERVA = 30 * 60;

    private final CommandDispatcher<UUID> representacionDispatcher;
    private final CommandDispatcher<UUID> reservaDispatcher;
    private final CommandDispatcher<String> clienteDispatcher;
    private final CommandDispatcher<UUID> pagoDispatcher;

    private final Repository<Reserva, UUID> reservaRepository;
    private final Repository<Cliente, String> clienteRepository;
    private final Repository<Pago, UUID> pagoRepository;

    private final EventConsumer<UUID> representacionEventConsumer;
    private final EventConsumer<UUID> reservaEventConsumer;
    private final EventConsumer<String> clienteEventConsumer;
    private final EventConsumer<UUID> pagoEventConsumer;

    private final Map<UUID, ScheduledFuture<?>> tareasTimeout;
    private final ThreadPoolTaskScheduler taskScheduler;

    private int timeout;

    public ReservaTeatroSaga(final CommandDispatcher<UUID> representacionDispatcher,
                             final CommandDispatcher<UUID> reservaDispatcher,
                             final CommandDispatcher<String> clienteDispatcher,
                             final CommandDispatcher<UUID> pagoDispatcher) {
        this.representacionDispatcher = representacionDispatcher;
        this.reservaDispatcher = reservaDispatcher;
        this.clienteDispatcher = clienteDispatcher;
        this.pagoDispatcher = pagoDispatcher;

        reservaRepository = RepositoryFactory.getRepository(Reserva.class);
        clienteRepository = RepositoryFactory.getRepository(Cliente.class);
        pagoRepository = RepositoryFactory.getRepository(Pago.class);

        representacionEventConsumer = (version, event) -> {
            if (event instanceof ButacasSeleccionadasEvent) {
                crearReserva((ButacasSeleccionadasEvent) event);
            }
        };
        reservaEventConsumer = (version, event) -> {
            if (event instanceof ReservaConfirmadaEvent) {
                aplicarDescuentos((ReservaConfirmadaEvent) event);
            } else if (event instanceof ReservaAbandonadaEvent) {
                if (!anularPago(event.getAggregateRootId())) {
                    liberarButacas(event.getAggregateRootId());
                }
            } else if (event instanceof ReservaCanceladaEvent) {
                detenerTareaTimeout(event.getAggregateRootId());
                if (!anularPago(event.getAggregateRootId())) {
                    liberarButacas(event.getAggregateRootId());
                }
            } else if (event instanceof ReservaPagadaEvent) {
                detenerTareaTimeout(event.getAggregateRootId());
            }
        };
        clienteEventConsumer = (version, event) -> {
            if (event instanceof DescuentosAplicadosEvent) {
                proponerPago((DescuentosAplicadosEvent) event);
            }
        };
        pagoEventConsumer = (version, event) -> {
            if (event instanceof PagoConfirmadoEvent) {
                pagarReserva((PagoConfirmadoEvent) event);
            } else if (event instanceof PagoAnuladoEvent) {
                liberarButacas(((PagoAnuladoEvent) event).getReserva());
            }
        };

        tareasTimeout = new ConcurrentHashMap<>();
        taskScheduler = new TaskSchedulerBuilder().build();
        taskScheduler.initialize();

        timeout = TIMEOUT_RESERVA;
    }

    public EventConsumer<UUID> getRepresentacionEventConsumer() {
        return representacionEventConsumer;
    }

    public EventConsumer<UUID> getReservaEventConsumer() {
        return reservaEventConsumer;
    }

    public EventConsumer<String> getClienteEventConsumer() {
        return clienteEventConsumer;
    }

    public EventConsumer<UUID> getPagoEventConsumer() {
        return pagoEventConsumer;
    }

    public void setTimeout(final Integer timeout) {
        this.timeout = timeout == null ? TIMEOUT_RESERVA : timeout;
    }

    private void crearReserva(final ButacasSeleccionadasEvent event) {
        iniciarTareaTimeout(event.getParaReserva());

        clienteDispatcher.dispatch(new RegistrarEmailCommand(event.getEmail()));
        reservaDispatcher.dispatch(new CrearReservaCommand(event.getParaReserva(),
                event.getAggregateRootId(), event.getButacas(), event.getEmail()));
    }

    private void liberarButacas(UUID idReserva) {
        final var reserva = reservaRepository.load(idReserva).orElseThrow();

        representacionDispatcher.dispatch(new LiberarButacasCommand(reserva.getRepresentacion(), reserva.getButacas()));
    }

    private void aplicarDescuentos(ReservaConfirmadaEvent event) {
        final var reserva = reservaRepository.load(event.getAggregateRootId()).orElseThrow();
        final var maximo = reserva.getButacas().stream()
                .mapToInt(Butaca::getPrecio)
                .sum();

        clienteDispatcher.dispatch(new AplicarDescuentosCommand(reserva.getCliente(), reserva.getId(), maximo));
    }

    private void proponerPago(DescuentosAplicadosEvent event) {
        final var reserva = reservaRepository.load(event.getEnReserva()).orElseThrow();
        final var cliente = clienteRepository.load(event.getAggregateRootId()).orElseThrow();
        final var conceptos = new ArrayList<Concepto>();

        for (final Butaca butaca : reserva.getButacas()) {
            conceptos.add(new Concepto("Butaca " + butaca.getFila() + butaca.getSilla(), butaca.getPrecio()));
        }
        for (final Descuento descuento : cliente.getDescuentos()) {
            if (event.getDescuentos().contains(descuento.getId())) { // Nos basamos en la info contenida en el evento
                conceptos.add(new Concepto(descuento.getDescripcion(), -descuento.getValor()));
            }
        }

        pagoDispatcher.dispatch(new ProponerPagoIdempotentCommand(
                UUID.randomUUID(), event.getEnReserva(), event.getAggregateRootId(), conceptos));
    }

    private boolean anularPago(final UUID idReserva) {
        final var pagos = pagoRepository.find(p -> p.getReserva().equals(idReserva));
        if (pagos.isEmpty()) {
            return false;
        }

        pagoDispatcher.dispatch(new AnularPagoCommand(pagos.get(0).getId(), idReserva));
        return true;
    }

    private void pagarReserva(final PagoConfirmadoEvent event) {
        reservaDispatcher.dispatch(new PagarReservaCommand(event.getReserva()));
    }

    private void iniciarTareaTimeout(final UUID idReserva) {
        tareasTimeout.computeIfAbsent(idReserva, i -> taskScheduler.schedule(() -> {
            LOG.info("Abandonando reserva {}", i);
            reservaDispatcher.dispatch(new AbandonarReservaCommand(idReserva));
        }, Instant.now().plusSeconds(timeout)));
    }

    private void detenerTareaTimeout(final UUID idReserva) {
        final var tareaTimeout = tareasTimeout.remove(idReserva);
        if (tareaTimeout != null) {
            tareaTimeout.cancel(false);
        }
    }

    @Override
    public void close() {
        tareasTimeout.clear();
        taskScheduler.shutdown();
    }
}
