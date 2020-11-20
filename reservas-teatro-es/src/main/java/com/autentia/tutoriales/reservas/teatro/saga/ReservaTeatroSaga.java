package com.autentia.tutoriales.reservas.teatro.saga;

import com.autentia.tutoriales.reservas.teatro.command.cliente.Cliente;
import com.autentia.tutoriales.reservas.teatro.command.cliente.RegistrarEmailCommand;
import com.autentia.tutoriales.reservas.teatro.command.representacion.ButacasSeleccionadasEvent;
import com.autentia.tutoriales.reservas.teatro.command.reserva.CancelarReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.CrearReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaCanceladaEvent;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.Closeable;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class ReservaTeatroSaga implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(ReservaTeatroSaga.class);

    private static final int TIMEOUT_RESERVA = 30 * 60;

    private final CommandDispatcher<Reserva, UUID> reservaDispatcher;
    private final CommandDispatcher<Cliente, String> clienteDispatcher;

    private final EventConsumer<UUID> representacionEventConsumer;
    private final EventConsumer<UUID> reservaEventConsumer;
    private final EventConsumer<String> clienteEventConsumer;

    private final Map<UUID, ScheduledFuture<?>> tareasTimeout;
    private final ThreadPoolTaskScheduler taskScheduler;

    private int timeout;

    public ReservaTeatroSaga(final CommandDispatcher<Reserva, UUID> reservaDispatcher,
                             final CommandDispatcher<Cliente, String> clienteDispatcher) {
        this.reservaDispatcher = reservaDispatcher;
        this.clienteDispatcher = clienteDispatcher;

        representacionEventConsumer = (version, event) -> {
            if (event instanceof ButacasSeleccionadasEvent) {
                crearReserva((ButacasSeleccionadasEvent) event);
            }
        };
        reservaEventConsumer = (version, event) -> {
            if (event instanceof ReservaCanceladaEvent) {
                cancelarTareaTimeout((ReservaCanceladaEvent) event);
            }
        };
        clienteEventConsumer = (version, event) -> {
            // Vacío por ahora...
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
        final var idReserva = UUID.randomUUID();

        // TODO: Recordar cancelar la tarea cuando se pague la reserva
        tareasTimeout.computeIfAbsent(idReserva, i -> taskScheduler.schedule(() -> {
            LOG.info("Cancelando reserva {} por timeout", i);
            reservaDispatcher.dispatch(new CancelarReservaCommand(idReserva, true));
        }, Instant.now().plusSeconds(timeout)));

        clienteDispatcher.dispatch(new RegistrarEmailCommand(event.getEmail()));
        reservaDispatcher.dispatch(new CrearReservaCommand(idReserva,
                event.getAggregateRootId(), event.getButacas(), event.getEmail()));
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
