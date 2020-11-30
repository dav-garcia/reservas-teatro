package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.event.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaCreadaEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class CrearReservaCommand implements Command<ReservaCommandContext, Reserva, UUID> {

    UUID aggregateRootId;
    UUID representacion;
    Set<Butaca> butacas;
    String cliente;

    @Override
    public void execute(final ReservaCommandContext context) {
        if (context.getRepository().load(aggregateRootId).isPresent()) {
            throw new CommandNotValidException("Reserva ya existe");
        }

        context.getEventPublisher().tryPublish(0L,
                new ReservaCreadaEvent(aggregateRootId, representacion, butacas, cliente));
    }
}
