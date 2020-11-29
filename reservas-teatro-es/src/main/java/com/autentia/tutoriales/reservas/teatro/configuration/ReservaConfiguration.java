package com.autentia.tutoriales.reservas.teatro.configuration;

import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaEventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.occ.OccCommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.inmemory.InMemoryEventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class ReservaConfiguration {

    @Bean
    public Repository<Reserva, UUID> reservaRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public InMemoryEventPublisher<UUID> reservaPublisher() {
        return new InMemoryEventPublisher<>();
    }

    @Bean
    public ReservaEventConsumer reservaEventConsumer(final Repository<Reserva, UUID> reservaRepository,
                                                     final InMemoryEventPublisher<UUID> reservaPublisher) {
        final var result = new ReservaEventConsumer(reservaRepository);
        reservaPublisher.registerEventConsumer(result);
        return result;
    }

    @Bean
    public ReservaCommandContext reservaCommandContext(final Repository<Reserva, UUID> reservaRepository,
                                                       final InMemoryEventPublisher<UUID> reservaPublisher) {
        return new ReservaCommandContext(reservaRepository, reservaPublisher);
    }

    @Bean
    public CommandDispatcher<ReservaCommandContext, Reserva, UUID> reservaDispatcher(
            final ReservaCommandContext context) {
        return new OccCommandDispatcher<>(context);
    }
}
