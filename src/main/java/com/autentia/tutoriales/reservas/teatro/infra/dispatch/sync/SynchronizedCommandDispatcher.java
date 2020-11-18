package com.autentia.tutoriales.reservas.teatro.infra.dispatch.sync;

import com.autentia.tutoriales.reservas.teatro.error.CommandException;
import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;

public class SynchronizedCommandDispatcher<T extends AggregateRoot<U>, U> implements CommandDispatcher<T, U> {

    private final Repository<T, U> repository;
    private final EventPublisher<U> eventPublisher;

    public SynchronizedCommandDispatcher(final Repository<T, U> repository, final EventPublisher<U> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public synchronized void dispatch(final Command<T, U> command) {
        try {
            command.execute(repository, eventPublisher);
        } catch (InconsistentStateException e) {
            throw new CommandException("Excepción imposible en entorno singleton", e);
        }
    }
}
