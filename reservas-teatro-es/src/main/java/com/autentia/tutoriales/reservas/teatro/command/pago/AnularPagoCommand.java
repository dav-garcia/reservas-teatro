package com.autentia.tutoriales.reservas.teatro.command.pago;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.pago.PagoAnuladoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import lombok.Value;

import java.util.UUID;

@Value
public class AnularPagoCommand implements Command<PagoCommandContext, Pago, UUID> {

    UUID aggregateRootId;

    @Override
    public void execute(final PagoCommandContext context) {
        final var pago = context.getRepository().load(aggregateRootId)
                .orElseThrow(() -> new CommandNotValidException("El pago no existe o ya se ha pagado"));

        context.getPaymentGateway().cancelPayment(pago.getCodigoPago());

        context.getEventPublisher().tryPublish(pago.getVersion(),
                new PagoAnuladoEvent(aggregateRootId));
    }
}
