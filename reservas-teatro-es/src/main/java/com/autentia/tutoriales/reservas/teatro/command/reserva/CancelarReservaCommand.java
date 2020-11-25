package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.RepositoryFactory;
import lombok.Value;

import java.util.UUID;

@Value
public class CancelarReservaCommand implements Command<UUID> {

    UUID aggregateRootId;
    boolean abandonada;

    @Override
    public void execute(EventPublisher<UUID> eventPublisher) {
        final var repository = RepositoryFactory.getRepository(Reserva.class);
        final var reserva = repository.load(aggregateRootId)
                .orElseThrow(() -> new CommandNotValidException("No existe la reserva"));

        eventPublisher.tryPublish(reserva.getVersion(), new ReservaCanceladaEvent(aggregateRootId, abandonada));
    }
}
