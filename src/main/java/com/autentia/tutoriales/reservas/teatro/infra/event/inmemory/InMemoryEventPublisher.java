package com.autentia.tutoriales.reservas.teatro.infra.event.inmemory;

import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryEventPublisher<U> implements EventPublisher<U> {

    private final Set<EventConsumer<U>> eventConsumers;
    private final ConcurrentMap<U, AtomicLong> currentVersions;

    public InMemoryEventPublisher() {
        eventConsumers = new HashSet<>();
        currentVersions = new ConcurrentHashMap<>();
    }

    public void registerEventConsumer(final EventConsumer<U> eventConsumer) {
        eventConsumers.add(eventConsumer);
    }

    @Override
    public void tryPublish(final long expectedVersion, final Event<U> event) {
        final var newVersion = expectedVersion + 1L;
        final var currentVersion = casCurrentVersion(event.getAggregateRootId(), expectedVersion, newVersion);

        if (expectedVersion == currentVersion) {
            notifyEventConsumers(newVersion, event);
        } else {
            throw new InconsistentStateException(
                    String.format("Versiones no coinciden: %d vs %d", expectedVersion, currentVersion));
        }
    }

    private long casCurrentVersion(final U id, long expectedVersion, long newVersion) {
        final var currentVersion = currentVersions.computeIfAbsent(id, i -> new AtomicLong(expectedVersion));
        return currentVersion.compareAndExchange(expectedVersion, newVersion);
    }

    private void notifyEventConsumers(final long version, final Event<U> event) {
        eventConsumers.forEach(c -> c.consume(version,  event));
    }
}
