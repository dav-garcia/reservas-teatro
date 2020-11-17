package com.autentia.tutoriales.reservas.teatro.infra.dispatch.syncrhonized;

import com.autentia.tutoriales.reservas.teatro.error.CommandException;
import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRootRegistry;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.EventSourceId;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventStreamFactory;

public class SynchronizedCommandDispatcher implements CommandDispatcher {

    private final EventStreamFactory eventStreamFactory;
    private final AggregateRootRegistry registry;

    public SynchronizedCommandDispatcher(final EventStreamFactory eventStreamFactory) {
        this.eventStreamFactory = eventStreamFactory;
        registry = new AggregateRootRegistry();
    }

    @Override
    public <T extends AggregateRoot<U>, U> void registerAggregateRoot(final Class<T> type, final Repository<T, U> repository) {
        registry.register(type, repository);
    }

    @Override
    public synchronized <T extends AggregateRoot<U>, U> void dispatch(final Command<T, U> command, final EventSourceId<T, U> id) {
        try {
            command.execute(id.getValue(), registry.getRepository(id.getType()), eventStreamFactory.getEventStream(id));
        } catch (InconsistentStateException e) {
            throw new CommandException("Excepción imposible en entorno singleton", e);
        }
    }
}
