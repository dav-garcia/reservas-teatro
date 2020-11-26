package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaAbandonadaEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.RepositoryFactory;
import lombok.Value;

import java.util.UUID;

@Value
public class AbandonarReservaCommand implements Command<UUID> {

    UUID aggregateRootId;

    @Override
    public void execute(EventPublisher<UUID> eventPublisher) {
        final var repository = RepositoryFactory.getRepository(Reserva.class);
        final var reserva = repository.load(aggregateRootId)
                .filter(r -> r.getEstado() != Reserva.Estado.PAGADA)
                .orElseThrow(() -> new CommandNotValidException("No se puede abandonar una reserva pagada"));

        eventPublisher.tryPublish(reserva.getVersion(), new ReservaAbandonadaEvent(aggregateRootId));
    }
}
