package com.autentia.tutoriales.reservas.teatro.infra.dispatch;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import org.springframework.lang.NonNull;

/**
 * El contexto del comando da acceso a los servicios e infraestructura que
 * el comando necesita para ejecutarse
 * <br>
 * Similar al {@code ApplicationContext} de Spring
 */
public class CommandContext<T extends AggregateRoot<U>, U> {

    private final Repository<T, U> repository;
    private final EventPublisher<U> eventPublisher;

    public CommandContext(final Repository<T, U> repository, final EventPublisher<U> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @NonNull
    public Repository<T, U> getRepository() {
        return repository;
    }

    @NonNull
    public EventPublisher<U> getEventPublisher() {
        return eventPublisher;
    }
}
