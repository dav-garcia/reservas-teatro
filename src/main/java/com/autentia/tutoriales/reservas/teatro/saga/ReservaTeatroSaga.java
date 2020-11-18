package com.autentia.tutoriales.reservas.teatro.saga;

import com.autentia.tutoriales.reservas.teatro.command.representacion.ButacasSeleccionadasEvent;
import com.autentia.tutoriales.reservas.teatro.command.reserva.CrearReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;

import java.util.UUID;

public class ReservaTeatroSaga implements EventConsumer<UUID> {

    private final CommandDispatcher<Reserva, UUID> reservaDispatcher;

    public ReservaTeatroSaga(final CommandDispatcher<Reserva, UUID> reservaDispatcher) {
        this.reservaDispatcher = reservaDispatcher;
    }

    @Override
    public void consume(final long version, final Event<UUID> event) {
        if (event instanceof ButacasSeleccionadasEvent) {
            crearReserva((ButacasSeleccionadasEvent) event);
        }
    }

    private void crearReserva(final ButacasSeleccionadasEvent event) {
        reservaDispatcher.dispatch(new CrearReservaCommand(UUID.randomUUID(), event.getAggregateRootId(), event.getButacas()));
    }
}
