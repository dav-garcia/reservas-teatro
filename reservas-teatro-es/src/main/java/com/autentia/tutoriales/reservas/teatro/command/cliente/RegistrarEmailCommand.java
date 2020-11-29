package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.event.cliente.EmailRegistradoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import lombok.Value;

@Value
public class RegistrarEmailCommand implements Command<ClienteCommandContext, Cliente, String> {

    String aggregateRootId;

    @Override
    public void execute(final ClienteCommandContext context) {
        if (context.getRepository().load(aggregateRootId).isEmpty()) { // No hace nada si email ya registrado
            context.getEventPublisher().tryPublish(0L,
                    new EmailRegistradoEvent(aggregateRootId));
        }
    }
}
