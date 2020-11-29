package com.autentia.tutoriales.reservas.teatro.command.pago;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.pago.PagoConfirmadoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import lombok.Value;

import java.util.UUID;

@Value
public class ConfirmarPagoCommand implements Command<PagoCommandContext, Pago, UUID> {

    UUID aggregateRootId;

    @Override
    public void execute(final PagoCommandContext context) {
        final var pago = context.getRepository().load(aggregateRootId)
                .orElseThrow(() -> new CommandNotValidException("El pago ya se ha confirmado"));

        context.getEventPublisher().tryPublish(pago.getVersion(),
                new PagoConfirmadoEvent(aggregateRootId));
    }
}
