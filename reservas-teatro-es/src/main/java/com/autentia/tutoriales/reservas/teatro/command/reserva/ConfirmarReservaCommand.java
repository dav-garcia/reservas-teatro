package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import lombok.Value;

import java.util.UUID;

@Value
public class ConfirmarReservaCommand implements Command<Reserva, UUID> {

    UUID aggregateRootId;

    @Override
    public void execute(final Repository<Reserva, UUID> repository, final EventPublisher<UUID> eventPublisher) {
        final var reserva = repository.load(aggregateRootId)
                .filter(r -> !r.isConfirmada())
                .orElseThrow(() -> new CommandNotValidException("No existe la reserva o ya está confirmada"));

        eventPublisher.tryPublish(reserva.getVersion(), new ReservaConfirmadaEvent(aggregateRootId));
    }
}