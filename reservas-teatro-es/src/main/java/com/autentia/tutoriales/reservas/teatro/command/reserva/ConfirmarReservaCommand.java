package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaConfirmadaEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import lombok.Value;

import java.util.UUID;

@Value
public class ConfirmarReservaCommand implements Command<UUID> {

    UUID aggregateRootId;

    @Override
    public void execute(final EventPublisher<UUID> eventPublisher) {
        final var reserva = ReservaCommandSupport.getRepository().load(aggregateRootId)
                .filter(r -> r.getEstado() == Reserva.Estado.CREADA)
                .orElseThrow(() -> new CommandNotValidException("No se puede confirmar la reserva"));

        eventPublisher.tryPublish(reserva.getVersion(), new ReservaConfirmadaEvent(aggregateRootId));
    }
}
