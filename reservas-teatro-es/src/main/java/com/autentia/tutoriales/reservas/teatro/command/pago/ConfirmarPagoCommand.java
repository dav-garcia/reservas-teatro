package com.autentia.tutoriales.reservas.teatro.command.pago;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.pago.PagoConfirmadoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.RepositoryFactory;
import lombok.Value;

import java.util.UUID;

@Value
public class ConfirmarPagoCommand implements Command<UUID> {

    UUID aggregateRootId;

    @Override
    public void execute(final EventPublisher<UUID> eventPublisher) {
        final var repository = RepositoryFactory.getRepository(Pago.class);
        final var pago = repository.load(aggregateRootId)
                .orElseThrow(() -> new CommandNotValidException("El pago ya se ha confirmado"));

        eventPublisher.tryPublish(pago.getVersion(), new PagoConfirmadoEvent(aggregateRootId));
    }
}
