package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.RepositoryFactory;
import lombok.Value;

@Value
public class RegistrarEmailCommand implements Command<String> {

    String aggregateRootId;

    @Override
    public void execute(EventPublisher<String> eventPublisher) {
        final var repository = RepositoryFactory.getRepository(Cliente.class);

        if (repository.load(aggregateRootId).isEmpty()) { // No hace nada si el email ya está registrado
            eventPublisher.tryPublish(0L, new EmailRegistradoEvent(aggregateRootId));
        }
    }
}
