package com.autentia.tutoriales.reservas.teatro.infra.dispatch.occ;

import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRootRegistry;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.EventSourceId;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventStreamFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccCommandDispatcher implements CommandDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(OccCommandDispatcher.class);

    private final EventStreamFactory eventStreamFactory;
    private final AggregateRootRegistry registry;

    public OccCommandDispatcher(final EventStreamFactory eventStreamFactory) {
        this.eventStreamFactory = eventStreamFactory;
        registry = new AggregateRootRegistry();
    }

    @Override
    public <T extends AggregateRoot<U>, U> void registerAggregateRoot(final Class<T> type, final Repository<T, U> repository) {
        registry.register(type, repository);
    }

    @Override
    public <T extends AggregateRoot<U>, U> void dispatch(final Command<T, U> command, final EventSourceId<T, U> id) {
        final var repository = registry.getRepository(id.getType());
        final var eventStream = eventStreamFactory.getEventStream(id);

        var retry = true;
        while (retry) {
            try {
                command.execute(id.getValue(), repository, eventStream);
                retry = false;
            } catch (InconsistentStateException e) {
                LOG.warn("Reintentando comando por estado inconsistente", e);
            }
        }
    }
}
