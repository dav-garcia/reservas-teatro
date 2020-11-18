package com.autentia.tutoriales.reservas.teatro.infra.stream.inmemory;

import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventPublisher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryEventPublisher<U> implements EventPublisher<U> {

    private final Set<EventConsumer<U>> eventConsumers;
    private final ConcurrentMap<U, AtomicLong> latestVersions;

    public InMemoryEventPublisher() {
        eventConsumers = new HashSet<>();
        latestVersions = new ConcurrentHashMap<>();
    }

    public void registerEventConsumer(final EventConsumer<U> eventConsumer) {
        eventConsumers.add(eventConsumer);
    }

    @Override
    public void tryPublish(final U id, final long expectedVersion, final List<Event> events) {
        final var latestVersion = latestVersions.computeIfAbsent(id, i -> new AtomicLong(0));

        long currentVersion = expectedVersion;
        for (final Event event : events) {
            final var realVersion = latestVersion.compareAndExchange(currentVersion, currentVersion + 1L);
            if (currentVersion == realVersion) {
                notifyEventConsumers(id, ++currentVersion, event);
            } else {
                throw new InconsistentStateException(
                        String.format("Versiones no coinciden: %d vs %d", currentVersion, realVersion));
            }
        }
    }

    private void notifyEventConsumers(final U id, final long version, final Event event) {
        eventConsumers.forEach(c -> c.consume(id, version,  event));
    }
}
