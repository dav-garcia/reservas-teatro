package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.event.cliente.EmailRegistradoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import lombok.Value;

@Value
public class RegistrarEmailCommand implements Command<String> {

    String aggregateRootId;

    @Override
    public void execute(EventPublisher<String> eventPublisher) {
        if (ClienteCommandSupport.getRepository().load(aggregateRootId).isEmpty()) { // No hace nada si email ya registrado
            eventPublisher.tryPublish(0L, new EmailRegistradoEvent(aggregateRootId));
        }
    }
}
