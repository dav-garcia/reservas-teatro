package com.autentia.tutoriales.reservas.teatro.infra.payment.autoaccept;

import com.autentia.tutoriales.reservas.teatro.infra.payment.PaymentGateway;
import com.autentia.tutoriales.reservas.teatro.infra.payment.PaymentGatewayFactory;

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
