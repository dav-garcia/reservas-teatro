package com.autentia.tutoriales.reservas.teatro.infra.payment.autoaccept;

import com.autentia.tutoriales.reservas.teatro.infra.payment.PaymentGateway;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementación que acepta los pagos automáticamente después de 1 segundo
 */
public class InMemoryPaymentGateway implements PaymentGateway {

    private static final String ERROR_MESSAGE = "Código de pago desconocido";

    private final Map<String, Status> payments;

    public InMemoryPaymentGateway() {
        payments = new HashMap<>();
    }

    @Override
    public String initiatePayment(final String email, final String description, final int value) {
        final var paymentId = RandomStringUtils.randomAlphanumeric(8);
        payments.put(paymentId, Status.PENDING);
        return paymentId;
    }

    @Override
    public Status getPaymentStatus(final String paymentId) {
        if (payments.containsKey(paymentId)) {
            return payments.get(paymentId);
        }
        throw new IllegalArgumentException(ERROR_MESSAGE);
    }

    @Override
    public boolean cancelPayment(final String paymentId) {
        if (payments.containsKey(paymentId)) {
            return payments.replace(paymentId, Status.PENDING, Status.CANCELLED);
        }
        throw new IllegalArgumentException(ERROR_MESSAGE);
    }

    public void setAccepted(final String paymentId) {
        if (payments.containsKey(paymentId)) {
            payments.put(paymentId, Status.ACCEPTED);
        } else {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
    }
}
