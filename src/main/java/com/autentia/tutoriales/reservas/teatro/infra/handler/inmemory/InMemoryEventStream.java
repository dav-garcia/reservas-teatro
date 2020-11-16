package com.autentia.tutoriales.reservas.teatro.infra.handler.inmemory;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRootRegistry;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.EventSourceId;
import com.autentia.tutoriales.reservas.teatro.infra.handler.EventStream;

import java.util.ArrayList;
import java.util.List;

public class InMemoryEventStream<T extends AggregateRoot<U>, U> implements EventStream<T, U> {

    private final AggregateRootRegistry registry;
    private final EventSourceId<T, U> id;
    private final List<Event<T, U>> pastEvents;
    private long latestVersion;

    public InMemoryEventStream(final AggregateRootRegistry registry, final EventSourceId<T, U> id) {
        this.registry = registry;
        this.id = id;
        pastEvents = new ArrayList<>();
        latestVersion = 0;
    }

    @Override
    public long getLatestVersion() {
        return latestVersion;
    }

    @Override
    public boolean tryPublish(final long currentVersion, final List<Event<T, U>> events) {
        if (currentVersion == latestVersion) {
            final var repository = registry.getRepository(id);
            events.forEach(e -> {
                pastEvents.add(e);
                e.apply(repository, ++latestVersion);
            });
            return true;
        }
        return false;
    }
}
