package com.autentia.tutoriales.reservas.teatro.command.pago;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.pago.Concepto;
import com.autentia.tutoriales.reservas.teatro.event.pago.PagoPropuestoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.payment.PaymentGateway;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class ProponerPagoRollbackCommand implements Command<PagoCommandContext, Pago, UUID> {

    UUID aggregateRootId;
    UUID reserva;
    String cliente;
    List<Concepto> conceptos;

    @Override
    public void execute(final PagoCommandContext context) {
        if (context.getRepository().load(aggregateRootId).isPresent()) {
            throw new CommandNotValidException("El pago ya ha sido propuesto");
        }

        final var codigoPago = iniciarPago(context.getPaymentGateway());
        try {
            context.getEventPublisher().tryPublish(0L,
                    new PagoPropuestoEvent(aggregateRootId, reserva, cliente, conceptos, codigoPago));
        } catch (Exception exception) { // Rollback si no se puede guardar el nuevo estado
            cancelarPago(context.getPaymentGateway(), codigoPago);
        }
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
