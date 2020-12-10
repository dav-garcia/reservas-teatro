package com.autentia.tutoriales.reservas.teatro.configuration;

import com.autentia.tutoriales.reservas.teatro.Representacion;
import com.autentia.tutoriales.reservas.teatro.Reserva;
import com.autentia.tutoriales.reservas.teatro.command.RepresentacionCommandService;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.event.inmemory.InMemoryEventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.DummyPlatformTransactionManager;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.UUID;

@Configuration
@EnableTransactionManagement
public class EcstConfiguration {

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DummyPlatformTransactionManager();
    }

    @Bean
    public Repository<Representacion, UUID> representacionRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public Repository<Reserva, UUID> reservaRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public InMemoryEventPublisher<UUID> reservaPublisher() {
        return new InMemoryEventPublisher<>();
    }

    @Bean
    public RepresentacionCommandService representacionCommandService(
            final Repository<Representacion, UUID> representacionRepository,
            final Repository<Reserva, UUID> reservaRepository,
            final EventPublisher<UUID> reservaPublisher) {
        return new RepresentacionCommandService(representacionRepository, reservaRepository, reservaPublisher);
    }
}
