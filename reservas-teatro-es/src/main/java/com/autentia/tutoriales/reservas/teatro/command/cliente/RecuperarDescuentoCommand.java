package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import lombok.Value;

import java.util.UUID;

@Value
public class RecuperarDescuentoCommand implements Command<Cliente, String> {

    String aggregateRootId;
    UUID descuento;

    @Override
    public void execute(final Repository<Cliente, String> repository, final EventPublisher<String> eventPublisher) {
        final var cliente = repository.load(aggregateRootId).orElseThrow();
        if (cliente.getDescuentos().stream().noneMatch(d -> d.getId().equals(descuento)
                && d.consumido)) {
            throw new CommandNotValidException("No se puede recuperar el descuento");
        }

        eventPublisher.tryPublish(cliente.getVersion(), new DescuentoRecuperadoEvent(aggregateRootId, descuento));
    }
}
