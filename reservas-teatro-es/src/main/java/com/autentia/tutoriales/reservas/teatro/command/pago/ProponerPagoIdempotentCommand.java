package com.autentia.tutoriales.reservas.teatro.command.pago;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.pago.PagoPropuestoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.payment.PaymentGateway;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.List;
import java.util.UUID;

@Value
@RequiredArgsConstructor
public class ProponerPagoIdempotentCommand implements Command<PagoCommandContext, Pago, UUID> {

    UUID aggregateRootId;
    UUID reserva;
    String cliente;
    List<Concepto> conceptos;

    @NonFinal
    String codigoPago;

    @Override
    public void execute(final PagoCommandContext context) {
        if (context.getRepository().load(aggregateRootId).isPresent()) {
            if (codigoPago != null) {
                cancelarPago(context.getPaymentGateway(), codigoPago);
            }
            throw new CommandNotValidException("El pago ya ha sido propuesto");
        }

        if (codigoPago == null) { // Idempotencia con el proveedor externo en caso de repetición
            codigoPago = iniciarPago(context.getPaymentGateway());
        }
        context.getEventPublisher().tryPublish(0L,
                new PagoPropuestoEvent(aggregateRootId, reserva, cliente, conceptos, codigoPago));
    }

    private String iniciarPago(final PaymentGateway paymentGateway) {
        final var descripcion = "Reserva " + reserva;
        final var valor = conceptos.stream()
                .mapToInt(Concepto::getPrecio)
                .sum();

        return paymentGateway.initiatePayment(cliente, descripcion, valor);
    }

    private void cancelarPago(final PaymentGateway paymentGateway, final String codigoPago) {
        paymentGateway.cancelPayment(codigoPago);
    }
}
