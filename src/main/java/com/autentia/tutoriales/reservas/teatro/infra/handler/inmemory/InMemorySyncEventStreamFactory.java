package com.autentia.tutoriales.reservas.teatro.infra.handler.inmemory;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.EventHandler;
import com.autentia.tutoriales.reservas.teatro.infra.handler.EventStream;
import com.autentia.tutoriales.reservas.teatro.infra.handler.EventStreamFactory;
import com.autentia.tutoriales.reservas.teatro.infra.handler.EventStreamId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemorySyncEventStreamFactory implements EventStreamFactory {

    private final Map<Class<? extends AggregateRoot<?>>, EventHandler> eventHandlerRegistry;
    private final Map<EventStreamId<?>, InMemoryEventStream> eventStreams;

    public InMemorySyncEventStreamFactory() {
        eventHandlerRegistry = new HashMap<>();
        eventStreams = new HashMap<>();
    }

    @Override
    public EventStream getForAggregateRoot(EventStreamId<?> id) {
        return eventStreams.computeIfAbsent(id, i -> {
            final var eventHandler = Optional.ofNullable(eventHandlerRegistry.get(i.getType()))
                    .orElseThrow(() -> new RuntimeException("No handler registered for type " + i.getType()));
            return new InMemoryEventStream(eventHandler);
        });
    }

    @Override
    public void subscribeToType(final Class<? extends AggregateRoot<?>> type, final EventHandler eventHandler) {
        eventHandlerRegistry.put(type, eventHandler);
    }
}
