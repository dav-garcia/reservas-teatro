package com.autentia.tutoriales.reservas.teatro.infra.stream.inmemory;

import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRootRegistry;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.EventSourceId;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventStream;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryEventStream<T extends AggregateRoot<U>, U> implements EventStream<T, U> {

    private final AggregateRootRegistry registry;
    private final EventSourceId<T, U> id;
    private final AtomicLong latestVersion;

    public InMemoryEventStream(final AggregateRootRegistry registry, final EventSourceId<T, U> id) {
        this.registry = registry;
        this.id = id;
        latestVersion = new AtomicLong(0);
    }

    @Override
    public void tryPublish(final long expectedVersion, final List<Event<T, U>> events) {
        final var repository = registry.getRepository(id.getType());
        long currentVersion = expectedVersion;
        for (final Event<T, U> event : events) {
            final var realVersion = latestVersion.compareAndExchange(currentVersion, currentVersion + 1L);
            if (currentVersion == realVersion) {
                event.apply(id.getValue(), ++currentVersion, repository);
            } else {
                throw new InconsistentStateException(
                        String.format("Versiones no coinciden: %d vs %d", currentVersion, realVersion));
            }
        }
    }
}
