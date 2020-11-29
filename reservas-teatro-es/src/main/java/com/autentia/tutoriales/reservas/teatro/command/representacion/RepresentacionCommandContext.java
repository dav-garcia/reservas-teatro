package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandContext;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.UUID;

public class RepresentacionCommandContext extends CommandContext<Representacion, UUID> {

    public RepresentacionCommandContext(final Repository<Representacion, UUID> repository,
                                        final EventPublisher<UUID> eventPublisher) {
        super(repository, eventPublisher);
    }
}
