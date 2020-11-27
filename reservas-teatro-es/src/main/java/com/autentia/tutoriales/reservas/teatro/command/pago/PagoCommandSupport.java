package com.autentia.tutoriales.reservas.teatro.command.pago;

import com.autentia.tutoriales.reservas.teatro.infra.payment.PaymentGateway;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import org.springframework.lang.NonNull;

import java.util.UUID;

/**
 * Para que los Command sean auténticos POJO, necesitan un apoyo para
 * poder acceder a los servicios de infraestructura
 */
public class PagoCommandSupport {

    private static Repository<Pago, UUID> repository;
    private static PaymentGateway paymentGateway;

    public static void setup(final Repository<Pago, UUID> repository, final PaymentGateway paymentGateway) {
        PagoCommandSupport.repository = repository;
        PagoCommandSupport.paymentGateway = paymentGateway;
    }

    @NonNull
    public static Repository<Pago, UUID> getRepository() {
        return repository;
    }

    @NonNull
    public static PaymentGateway getPaymentGateway() {
        return paymentGateway;
    }

    private PagoCommandSupport() {
        // Vacío
    }
}
