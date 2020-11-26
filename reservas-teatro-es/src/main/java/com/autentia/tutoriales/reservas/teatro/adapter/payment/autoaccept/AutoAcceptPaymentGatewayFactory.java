package com.autentia.tutoriales.reservas.teatro.adapter.payment.autoaccept;

import com.autentia.tutoriales.reservas.teatro.adapter.payment.PaymentGateway;
import com.autentia.tutoriales.reservas.teatro.adapter.payment.PaymentGatewayFactory;

public class AutoAcceptPaymentGatewayFactory extends PaymentGatewayFactory {

    private final PaymentGateway paymentGateway;

    public AutoAcceptPaymentGatewayFactory() {
        paymentGateway = new AutoAcceptPaymentGateway();
    }

    @Override
    public PaymentGateway get() {
        return paymentGateway;
    }
}
