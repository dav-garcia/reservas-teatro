package com.autentia.tutoriales.reservas.teatro.command.pago;

import com.autentia.tutoriales.reservas.teatro.adapter.payment.PaymentGatewayFactory;
import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.pago.PagoPropuestoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.RepositoryFactory;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.List;
import java.util.UUID;

@Value
@RequiredArgsConstructor
public class ProponerPagoIdempotentCommand implements Command<UUID> {

    UUID aggregateRootId;
    UUID reserva;
    String cliente;
    List<Concepto> conceptos;

    @NonFinal
    String codigoPago;

    @Override
    public void execute(final EventPublisher<UUID> eventPublisher) {
        final var repository = RepositoryFactory.getRepository(Pago.class);
        if (repository.load(aggregateRootId).isPresent()) {
            if (codigoPago != null) {
                cancelarPago(codigoPago);
            }
            throw new CommandNotValidException("El pago ya ha sido propuesto");
        }

        if (codigoPago == null) { // Idempotencia con el proveedor externo en caso de repetición
            codigoPago = iniciarPago();
        }
        eventPublisher.tryPublish(0L, new PagoPropuestoEvent(aggregateRootId, reserva, cliente, conceptos, codigoPago));
    }

    private String iniciarPago() {
        final var descripcion = "Reserva " + reserva;
        final var valor = conceptos.stream()
                .mapToInt(Concepto::getPrecio)
                .sum();

        return PaymentGatewayFactory.getPaymentGateway().initiatePayment(cliente, descripcion, valor);
    }

    private void cancelarPago(final String codigoPago) {
        PaymentGatewayFactory.getPaymentGateway().cancelPayment(codigoPago);
    }
}
