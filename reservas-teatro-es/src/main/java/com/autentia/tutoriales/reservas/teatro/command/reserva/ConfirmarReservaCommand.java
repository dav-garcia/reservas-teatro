package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaConfirmadaEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import lombok.Value;

import java.util.UUID;

@Value
public class ConfirmarReservaCommand implements Command<ReservaCommandContext, Reserva, UUID> {

    UUID aggregateRootId;

    @Override
    public void execute(final ReservaCommandContext context) {
        final var reserva = context.getRepository().load(aggregateRootId)
                .filter(r -> r.getEstado() == Reserva.Estado.CREADA)
                .orElseThrow(() -> new CommandNotValidException("No se puede confirmar la reserva"));

        context.getEventPublisher().tryPublish(reserva.getVersion(),
                new ReservaConfirmadaEvent(aggregateRootId));
    }
}
