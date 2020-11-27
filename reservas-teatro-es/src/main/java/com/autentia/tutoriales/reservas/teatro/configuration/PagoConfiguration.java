package com.autentia.tutoriales.reservas.teatro.configuration;

import com.autentia.tutoriales.reservas.teatro.command.pago.Pago;
import com.autentia.tutoriales.reservas.teatro.command.pago.PagoCommandSupport;
import com.autentia.tutoriales.reservas.teatro.command.pago.PagoEventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.occ.OccCommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
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
    public PagoEventConsumer pagoEventConsumer(final Repository<Pago, UUID> pagoRepository) {
        return new PagoEventConsumer(pagoRepository);
    }

    @Bean
    public EventPublisher<UUID> pagoPublisher(final PagoEventConsumer pagoEventConsumer) {
        final var result = new InMemoryEventPublisher<UUID>();
        result.registerEventConsumer(pagoEventConsumer);
        return result;
    }

    @Bean
    public CommandDispatcher<UUID> pagoDispatcher(final Repository<Pago, UUID> pagoRepository,
                                                  final PaymentGateway paymentGateway,
                                                  final EventPublisher<UUID> pagoPublisher) {
        PagoCommandSupport.setup(pagoRepository, paymentGateway);
        return new OccCommandDispatcher<>(pagoPublisher);
    }

    @Bean
    public PaymentGateway paymentGateway() {
        return new AutoAcceptPaymentGateway();
    }
}
