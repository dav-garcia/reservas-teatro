package com.autentia.tutoriales.reservas.teatro.saga;

import com.autentia.tutoriales.reservas.teatro.command.representacion.ButacasSeleccionadasEvent;
import com.autentia.tutoriales.reservas.teatro.command.reserva.CrearReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventConsumer;

import java.util.UUID;

public class ReservaTeatroSaga implements EventConsumer<UUID> {

    private final CommandDispatcher<Reserva, UUID> reservaDispatcher;

    public ReservaTeatroSaga(final CommandDispatcher<Reserva, UUID> reservaDispatcher) {
        this.reservaDispatcher = reservaDispatcher;
    }

    @Override
    public void consume(final UUID id, final long version, final Event event) {
        if (event instanceof ButacasSeleccionadasEvent) {
            crearReserva(id, (ButacasSeleccionadasEvent) event);
        }
    }

    private void crearReserva(final UUID id, final ButacasSeleccionadasEvent event) {
        reservaDispatcher.dispatch(UUID.randomUUID(), new CrearReservaCommand(id, event.getButacas()));
    }
}
