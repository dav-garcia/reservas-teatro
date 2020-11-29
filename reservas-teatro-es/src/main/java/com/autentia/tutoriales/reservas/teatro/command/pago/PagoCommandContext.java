package com.autentia.tutoriales.reservas.teatro.command.pago;

import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandContext;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.payment.PaymentGateway;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import org.springframework.lang.NonNull;

import java.util.UUID;

public class PagoCommandContext extends CommandContext<Pago, UUID> {

    private final PaymentGateway paymentGateway;

    public PagoCommandContext(final Repository<Pago, UUID> repository,
                              final EventPublisher<UUID> eventPublisher,
                              final PaymentGateway paymentGateway) {
        super(repository, eventPublisher);
        this.paymentGateway = paymentGateway;
    }

    @NonNull
    public PaymentGateway getPaymentGateway() {
        return paymentGateway;
    }
}
