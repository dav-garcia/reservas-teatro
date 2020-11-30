package com.autentia.tutoriales.reservas.teatro.configuration;

import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.event.inmemory.InMemoryEventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepository;
import com.autentia.tutoriales.reservas.teatro.query.Historico;
import com.autentia.tutoriales.reservas.teatro.query.HistoricoEventConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class HistoricoConfiguration {

    @Bean
    public Repository<Historico, String> historicoRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public HistoricoEventConsumer historicoEventConsumer(final Repository<Historico, String> historicoRepository,
                                                         final InMemoryEventPublisher<UUID> representacionPublisher,
                                                         final InMemoryEventPublisher<UUID> reservaPublisher,
                                                         final InMemoryEventPublisher<String> clientePublisher,
                                                         final InMemoryEventPublisher<UUID> pagoPublisher) {
        final var result = new HistoricoEventConsumer(historicoRepository);
        representacionPublisher.registerEventConsumer((EventConsumer) result);
        reservaPublisher.registerEventConsumer((EventConsumer) result);
        clientePublisher.registerEventConsumer((EventConsumer) result);
        pagoPublisher.registerEventConsumer((EventConsumer) result);
        return result;
    }
}
