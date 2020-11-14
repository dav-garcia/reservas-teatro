package com.autentia.tutoriales.reservas.teatro.handler.inmemory;

import com.autentia.tutoriales.reservas.teatro.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.EventHandler;
import com.autentia.tutoriales.reservas.teatro.handler.EventStream;
import com.autentia.tutoriales.reservas.teatro.handler.EventStreamFactory;
import com.autentia.tutoriales.reservas.teatro.handler.EventStreamId;

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

    public void putEventHandler(final Class<? extends AggregateRoot<?>> type, final EventHandler eventHandler) {
        eventHandlerRegistry.put(type, eventHandler);
    }

    public void removeEventHandler(final Class<? extends AggregateRoot<?>> type) {
        eventHandlerRegistry.remove(type);
    }

    @Override
    public EventStream getForRoot(EventStreamId<?> id) {
        return eventStreams.computeIfAbsent(id, i -> {
            final var eventHandler = Optional.ofNullable(eventHandlerRegistry.get(i.getType()))
                    .orElseThrow(() -> new RuntimeException("No handler registered for type " + i.getType()));
            return new InMemoryEventStream(eventHandler);
        });
    }
}
