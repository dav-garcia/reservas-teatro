package com.autentia.tutoriales.reservas.teatro.configuration;

import com.autentia.tutoriales.reservas.teatro.command.pago.Pago;
import com.autentia.tutoriales.reservas.teatro.command.pago.PagoCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.pago.PagoEventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.occ.OccCommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.inmemory.InMemoryEventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.payment.PaymentGateway;
import com.autentia.tutoriales.reservas.teatro.infra.payment.autoaccept.AutoAcceptPaymentGateway;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class PagoConfiguration {

    @Bean
    public Repository<Pago, UUID> pagoRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public InMemoryEventPublisher<UUID> pagoPublisher() {
        return new InMemoryEventPublisher<>();
    }

    @Bean
    public PaymentGateway paymentGateway() {
        return new AutoAcceptPaymentGateway();
    }

    @Bean
    public PagoEventConsumer pagoEventConsumer(final Repository<Pago, UUID> pagoRepository,
                                               final InMemoryEventPublisher<UUID> pagoPublisher) {
        final var result = new PagoEventConsumer(pagoRepository);
        pagoPublisher.registerEventConsumer(result);
        return result;
    }

    @Bean
    public PagoCommandContext pagoCommandContext(final Repository<Pago, UUID> pagoRepository,
                                                 final InMemoryEventPublisher<UUID> pagoPublisher,
                                                 final PaymentGateway paymentGateway) {
        return new PagoCommandContext(pagoRepository, pagoPublisher, paymentGateway);
    }

    @Bean
    public CommandDispatcher<PagoCommandContext, Pago, UUID> pagoDispatcher(
            final PagoCommandContext context) {
        return new OccCommandDispatcher<>(context);
    }
}
