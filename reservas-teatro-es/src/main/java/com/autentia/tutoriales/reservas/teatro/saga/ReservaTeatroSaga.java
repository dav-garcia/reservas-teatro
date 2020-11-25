package com.autentia.tutoriales.reservas.teatro.saga;

import com.autentia.tutoriales.reservas.teatro.command.cliente.AplicarDescuentosCommand;
import com.autentia.tutoriales.reservas.teatro.command.cliente.Cliente;
import com.autentia.tutoriales.reservas.teatro.command.cliente.Descuento;
import com.autentia.tutoriales.reservas.teatro.command.cliente.DescuentosAplicadosEvent;
import com.autentia.tutoriales.reservas.teatro.command.cliente.RegistrarEmailCommand;
import com.autentia.tutoriales.reservas.teatro.command.pago.Concepto;
import com.autentia.tutoriales.reservas.teatro.command.pago.ProponerPagoCommand;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.command.representacion.ButacasSeleccionadasEvent;
import com.autentia.tutoriales.reservas.teatro.command.reserva.CancelarReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.CrearReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaCanceladaEvent;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaConfirmadaEvent;
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

    private final CommandDispatcher<UUID> reservaDispatcher;
    private final CommandDispatcher<String> clienteDispatcher;
    private final CommandDispatcher<UUID> pagoDispatcher;

    private final Repository<Reserva, UUID> reservaRepository;
    private final Repository<Cliente, String> clienteRepository;

    private final EventConsumer<UUID> representacionEventConsumer;
    private final EventConsumer<UUID> reservaEventConsumer;
    private final EventConsumer<String> clienteEventConsumer;

    private final Map<UUID, ScheduledFuture<?>> tareasTimeout;
    private final ThreadPoolTaskScheduler taskScheduler;

    private int timeout;

    public ReservaTeatroSaga(final CommandDispatcher<UUID> reservaDispatcher,
                             final CommandDispatcher<String> clienteDispatcher,
                             final CommandDispatcher<UUID> pagoDispatcher) {
        this.reservaDispatcher = reservaDispatcher;
        this.clienteDispatcher = clienteDispatcher;
        this.pagoDispatcher = pagoDispatcher;

        reservaRepository = RepositoryFactory.getRepository(Reserva.class);
        clienteRepository = RepositoryFactory.getRepository(Cliente.class);

        representacionEventConsumer = (version, event) -> {
            if (event instanceof ButacasSeleccionadasEvent) {
                crearReserva((ButacasSeleccionadasEvent) event);
            }
        };
        reservaEventConsumer = (version, event) -> {
            if (event instanceof ReservaConfirmadaEvent) {
                aplicarDescuentos((ReservaConfirmadaEvent) event);
            } else if (event instanceof ReservaCanceladaEvent) {
                cancelarTareaTimeout((ReservaCanceladaEvent) event);
            }
        };
        clienteEventConsumer = (version, event) -> {
            if (event instanceof DescuentosAplicadosEvent) {
                proponerPago((DescuentosAplicadosEvent) event);
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(final Integer timeout) {
        this.timeout = timeout == null ? TIMEOUT_RESERVA : timeout;
    }

    private void crearReserva(final ButacasSeleccionadasEvent event) {
        // TODO: Recordar cancelar la tarea cuando se pague la reserva
        tareasTimeout.computeIfAbsent(event.getParaReserva(), i -> taskScheduler.schedule(() -> {
            LOG.info("Cancelando reserva {} por timeout", i);
            reservaDispatcher.dispatch(new CancelarReservaCommand(event.getParaReserva(), true));
        }, Instant.now().plusSeconds(timeout)));

        clienteDispatcher.dispatch(new RegistrarEmailCommand(event.getEmail()));
        reservaDispatcher.dispatch(new CrearReservaCommand(event.getParaReserva(),
                event.getAggregateRootId(), event.getButacas(), event.getEmail()));
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

        pagoDispatcher.dispatch(new ProponerPagoCommand(UUID.randomUUID(), event.getEnReserva(), event.getAggregateRootId(), conceptos));
    }

    private void cancelarTareaTimeout(final ReservaCanceladaEvent event) {
        final var tareaTimeout = tareasTimeout.remove(event.getAggregateRootId());
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
