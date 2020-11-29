package com.autentia.tutoriales.reservas.teatro.infra.dispatch.sync;

import com.autentia.tutoriales.reservas.teatro.error.CommandException;
import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandContext;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;

public class SynchronizedCommandDispatcher<C extends CommandContext<T, U>, T extends AggregateRoot<U>, U>
        implements CommandDispatcher<C, T, U> {

    private final C context;

    public SynchronizedCommandDispatcher(final C context) {
        this.context = context;
    }

    public C getContext() {
        return context;
    }

    @Override
    public synchronized void dispatch(final Command<C, T, U> command) {
        try {
            command.execute(context);
        } catch (InconsistentStateException e) {
            throw new CommandException("Excepción imposible en entorno singleton", e);
        }
    }
}
