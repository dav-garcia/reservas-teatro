package com.autentia.tutoriales.reservas.teatro.adapter.payment;

public interface PaymentGateway {

    enum Status {
        PENDING,
        ACCEPTED,
        CANCELLED
    }

    String initiatePayment(final String email, final String description, final int value);
    Status getPaymentStatus(final  String paymentId);
    boolean cancelPayment(final String paymentId);
}
