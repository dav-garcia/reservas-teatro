package com.autentia.tutoriales.reservas.teatro.configuration;

import com.autentia.tutoriales.reservas.teatro.command.representacion.Representacion;
import com.autentia.tutoriales.reservas.teatro.command.representacion.RepresentacionCommandSupport;
import com.autentia.tutoriales.reservas.teatro.command.representacion.RepresentacionEventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.occ.OccCommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
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
    public RepresentacionEventConsumer representacionEventConsumer(final Repository<Representacion, UUID> representacionRepository) {
        return new RepresentacionEventConsumer(representacionRepository);
    }

    @Bean
    public EventPublisher<UUID> representacionPublisher(final RepresentacionEventConsumer representacionEventConsumer) {
        final var result = new InMemoryEventPublisher<UUID>();
        result.registerEventConsumer(representacionEventConsumer);
        return result;
    }

    @Bean
    public CommandDispatcher<UUID> representacionDispatcher(final Repository<Representacion, UUID> representacionRepository,
                                                            final EventPublisher<UUID> representacionPublisher) {
        RepresentacionCommandSupport.setup(representacionRepository);
        return new OccCommandDispatcher<>(representacionPublisher);
    }
}
