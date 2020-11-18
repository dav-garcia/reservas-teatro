package com.autentia.tutoriales.reservas.teatro.saga;

import com.autentia.tutoriales.reservas.teatro.command.cliente.Cliente;
import com.autentia.tutoriales.reservas.teatro.command.cliente.RegistrarEmailCommand;
import com.autentia.tutoriales.reservas.teatro.command.representacion.ButacasSeleccionadasEvent;
import com.autentia.tutoriales.reservas.teatro.command.reserva.CrearReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;

import java.util.UUID;

public class ReservaTeatroSaga {


    private final CommandDispatcher<Reserva, UUID> reservaDispatcher;
    private final CommandDispatcher<Cliente, String> clienteDispatcher;
    private final EventConsumer<UUID> representacionEventConsumer;
    private final EventConsumer<UUID> reservaEventConsumer;
    private final EventConsumer<String> clienteEventConsumer;

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
            // Vacío por ahora...
        };
        clienteEventConsumer = (version, event) -> {
            // Vacío por ahora...
        };
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

    private void crearReserva(final ButacasSeleccionadasEvent event) {
        clienteDispatcher.dispatch(new RegistrarEmailCommand(event.getEmail()));
        reservaDispatcher.dispatch(new CrearReservaCommand(UUID.randomUUID(),
                event.getAggregateRootId(), event.getButacas(), event.getEmail()));
    }
}
