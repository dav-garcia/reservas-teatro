package com.autentia.tutoriales.reservas.teatro.infra.dispatch.occ;

import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccCommandDispatcher<T extends AggregateRoot<U>, U> implements CommandDispatcher<T, U> {

    private static final Logger LOG = LoggerFactory.getLogger(OccCommandDispatcher.class);

    private final Repository<T, U> repository;
    private final EventPublisher<U> eventPublisher;

    public OccCommandDispatcher(final Repository<T, U> repository, final EventPublisher<U> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void dispatch(final Command<T, U> command) {
        var retry = true;
        while (retry) {
            try {
                command.execute(repository, eventPublisher);
                retry = false;
            } catch (InconsistentStateException e) {
                LOG.warn("Reintentando comando por estado inconsistente", e);
            }
        }
    }
}
