package com.autentia.tutoriales.reservas.teatro.adapter.payment;

import com.autentia.tutoriales.reservas.teatro.adapter.payment.autoaccept.AutoAcceptPaymentGatewayFactory;

@SuppressWarnings("java:S1610")
public abstract class PaymentGatewayFactory {

    private static class InstanceHolder {
        public static final PaymentGatewayFactory INSTANCE = new AutoAcceptPaymentGatewayFactory();
    }

    public static PaymentGateway getPaymentGateway() {
        return InstanceHolder.INSTANCE.get();
    }

    public abstract PaymentGateway get();
}
