package com.autentia.tutoriales.reservas.teatro.infra.stream.inmemory;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRootRegistry;
import com.autentia.tutoriales.reservas.teatro.infra.EventSourceId;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventStream;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventStreamFactory;

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
    public <T extends AggregateRoot<U>, U> EventStream<T, U> getEventStream(EventSourceId<T, U> id) {
        return (EventStream<T, U>) eventStreams.computeIfAbsent(id, i -> new InMemoryEventStream<>(registry, id));
    }
}
