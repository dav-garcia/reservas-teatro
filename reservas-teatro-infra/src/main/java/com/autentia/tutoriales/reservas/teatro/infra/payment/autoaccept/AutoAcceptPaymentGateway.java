package com.autentia.tutoriales.reservas.teatro.infra.payment.autoaccept;

import com.autentia.tutoriales.reservas.teatro.infra.payment.PaymentGateway;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implementación que acepta los pagos automáticamente después de 1 segundo
 */
public class AutoAcceptPaymentGateway implements PaymentGateway {

    private final Map<String, Status> payments = new HashMap<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    @Override
    public String initiatePayment(final String email, final String description, final int value) {
        final var paymentId = RandomStringUtils.randomAlphanumeric(8);
        payments.put(paymentId, Status.PENDING);
        executorService.schedule(() -> setAccepted(paymentId), 1, TimeUnit.SECONDS);
        return paymentId;
    }

    private void setAccepted(String paymentId) {
        if (payments.containsKey(paymentId)) {
            payments.put(paymentId, Status.ACCEPTED);
        }
    }

    @Override
    public Status getPaymentStatus(final String paymentId) {
        if (payments.containsKey(paymentId)) {
            return payments.get(paymentId);
        }
        throw new IllegalArgumentException("Código de pago desconocido");
    }

    @Override
    public boolean cancelPayment(final String paymentId) {
        if (payments.containsKey(paymentId)) {
            return payments.replace(paymentId, Status.PENDING, Status.CANCELLED);
        }
        throw new IllegalArgumentException("Código de pago desconocido");
    }
}
