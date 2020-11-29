package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandContext;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.UUID;

public class ReservaCommandContext extends CommandContext<Reserva, UUID> {

    public ReservaCommandContext(final Repository<Reserva, UUID> repository,
                                 final EventPublisher<UUID> eventPublisher) {
        super(repository, eventPublisher);
    }
}
