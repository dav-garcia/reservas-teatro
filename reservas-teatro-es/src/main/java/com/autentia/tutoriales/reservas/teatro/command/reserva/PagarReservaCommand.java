package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaPagadaEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import lombok.Value;

import java.util.UUID;

@Value
public class PagarReservaCommand implements Command<UUID> {

    UUID aggregateRootId;

    @Override
    public void execute(EventPublisher<UUID> eventPublisher) {
        final var reserva = ReservaCommandSupport.getRepository().load(aggregateRootId)
                .filter(r -> r.getEstado() != Reserva.Estado.PAGADA)
                .orElseThrow(() -> new CommandNotValidException("No se puede pagar una reserva dos veces"));

        eventPublisher.tryPublish(reserva.getVersion(), new ReservaPagadaEvent(aggregateRootId));
    }
}
