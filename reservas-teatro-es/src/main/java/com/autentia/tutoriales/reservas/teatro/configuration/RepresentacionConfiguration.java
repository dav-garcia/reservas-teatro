package com.autentia.tutoriales.reservas.teatro.configuration;

import com.autentia.tutoriales.reservas.teatro.command.representacion.Representacion;
import com.autentia.tutoriales.reservas.teatro.command.representacion.RepresentacionCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.representacion.RepresentacionEventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.occ.OccCommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.inmemory.InMemoryEventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class RepresentacionConfiguration {

    @Bean
    public Repository<Representacion, UUID> representacionRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public InMemoryEventPublisher<UUID> representacionPublisher() {
        return new InMemoryEventPublisher<>();
    }

    @Bean
    public RepresentacionEventConsumer representacionEventConsumer(final Repository<Representacion, UUID> representacionRepository,
                                                                   final InMemoryEventPublisher<UUID> representacionPublisher) {
        final var result = new RepresentacionEventConsumer(representacionRepository);
        representacionPublisher.registerEventConsumer(result);
        return result;
    }

    @Bean
    public RepresentacionCommandContext representacionCommandContext(final Repository<Representacion, UUID> representacionRepository,
                                                                     final InMemoryEventPublisher<UUID> representacionPublisher) {
        return new RepresentacionCommandContext(representacionRepository, representacionPublisher);
    }

    @Bean
    public CommandDispatcher<RepresentacionCommandContext, Representacion, UUID> representacionDispatcher(
            final RepresentacionCommandContext context) {
        return new OccCommandDispatcher<>(context);
    }
}
