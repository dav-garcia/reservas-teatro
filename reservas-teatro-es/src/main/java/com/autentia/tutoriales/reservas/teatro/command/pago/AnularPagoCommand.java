package com.autentia.tutoriales.reservas.teatro.command.pago;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.pago.PagoAnuladoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.RepositoryFactory;
import lombok.Value;

import java.util.UUID;

@Value
public class AnularPagoCommand implements Command<UUID> {

    UUID aggregateRootId;
    UUID reserva;

    @Override
    public void execute(EventPublisher<UUID> eventPublisher) {
        final var repository = RepositoryFactory.getRepository(Pago.class);
        final var pago = repository.load(aggregateRootId)
                .orElseThrow(() -> new CommandNotValidException("El pago no existe o ya se ha pagado"));

        eventPublisher.tryPublish(pago.getVersion(), new PagoAnuladoEvent(aggregateRootId, reserva));
    }
}
