package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import lombok.Value;

@Value
public class RegistrarEmailCommand implements Command<Cliente, String> {

    String aggregateRootId;

    @Override
    public void execute(Repository<Cliente, String> repository, EventPublisher<String> eventPublisher) {
        if (repository.load(aggregateRootId).isEmpty()) { // No hace nada si el email ya está registrado
            eventPublisher.tryPublish(0L, new EmailRegistradoEvent(aggregateRootId));
        }
    }
}
