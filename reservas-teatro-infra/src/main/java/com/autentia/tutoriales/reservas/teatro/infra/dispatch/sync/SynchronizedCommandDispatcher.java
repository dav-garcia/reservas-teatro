package com.autentia.tutoriales.reservas.teatro.infra.dispatch.sync;

import com.autentia.tutoriales.reservas.teatro.error.CommandException;
import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;

public class SynchronizedCommandDispatcher<U> implements CommandDispatcher<U> {

    private final EventPublisher<U> eventPublisher;

    public SynchronizedCommandDispatcher(final EventPublisher<U> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public synchronized void dispatch(final Command<U> command) {
        try {
            command.execute(eventPublisher);
        } catch (InconsistentStateException e) {
            throw new CommandException("Excepción imposible en entorno singleton", e);
        }
    }
}
