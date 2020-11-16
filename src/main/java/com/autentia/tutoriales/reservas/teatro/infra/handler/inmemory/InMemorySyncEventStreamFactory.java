package com.autentia.tutoriales.reservas.teatro.infra.handler.inmemory;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRootRegistry;
import com.autentia.tutoriales.reservas.teatro.infra.EventSourceId;
import com.autentia.tutoriales.reservas.teatro.infra.handler.EventStream;
import com.autentia.tutoriales.reservas.teatro.infra.handler.EventStreamFactory;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.HashMap;
import java.util.Map;

public class InMemorySyncEventStreamFactory implements EventStreamFactory {

    private final AggregateRootRegistry registry;
    private final Map<EventSourceId<?, ?>, InMemoryEventStream<?, ?>> eventStreams;

    public InMemorySyncEventStreamFactory() {
        registry = new AggregateRootRegistry();
        eventStreams = new HashMap<>();
    }

    @Override
    public <T extends AggregateRoot<U>, U> void registerAggregateRoot(Class<T> type, Repository<T, U> repository) {
        registry.register(type, repository);
    }

    @Override
    public <T extends AggregateRoot<U>, U> EventStream<T, U> get(EventSourceId<T, U> id) {
        return (EventStream<T, U>) eventStreams.computeIfAbsent(id, i -> new InMemoryEventStream<>(registry, id));
    }
}
