package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandContext;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

public class ClienteCommandContext extends CommandContext<Cliente, String> {

    public ClienteCommandContext(final Repository<Cliente, String> repository,
                                 final EventPublisher<String> eventPublisher) {
        super(repository, eventPublisher);
    }
}
