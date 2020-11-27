package com.autentia.tutoriales.reservas.teatro.configuration;

import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaCommandSupport;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaEventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.occ.OccCommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.event.inmemory.InMemoryEventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepository;
import com.autentia.tutoriales.reservas.teatro.infra.task.TaskScheduler;
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
    public ReservaEventConsumer reservaEventConsumer(final Repository<Reserva, UUID> reservaRepository) {
        return new ReservaEventConsumer(reservaRepository);
    }

    @Bean
    public EventPublisher<UUID> reservaPublisher(final ReservaEventConsumer reservaEventConsumer) {
        final var result = new InMemoryEventPublisher<UUID>();
        result.registerEventConsumer(reservaEventConsumer);
        return result;
    }

    @Bean
    public CommandDispatcher<UUID> reservaDispatcher(final Repository<Reserva, UUID> reservaRepository,
                                                     final EventPublisher<UUID> reservaPublisher) {
        ReservaCommandSupport.setup(reservaRepository);
        return new OccCommandDispatcher<>(reservaPublisher);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new TaskScheduler();
    }
}
