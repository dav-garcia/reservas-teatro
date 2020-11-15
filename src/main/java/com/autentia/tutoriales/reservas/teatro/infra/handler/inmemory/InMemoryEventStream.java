package com.autentia.tutoriales.reservas.teatro.infra.handler.inmemory;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.EventHandler;
import com.autentia.tutoriales.reservas.teatro.infra.handler.EventStream;

import java.util.ArrayList;
import java.util.List;

public class InMemoryEventStream implements EventStream {

    private final EventHandler eventHandler;
    private final List<Event> pastEvents;
    private long latestVersion;

    public InMemoryEventStream(final EventHandler eventHandler) {
        this.eventHandler = eventHandler;
        pastEvents = new ArrayList<>();
        latestVersion = 0;
    }

    @Override
    public long getLatestVersion() {
        return latestVersion;
    }

    @Override
    public boolean tryPublish(final long currentVersion, final List<Event> events) {
        if (currentVersion == latestVersion) {
            pastEvents.addAll(events);
            events.forEach(e -> eventHandler.apply(++latestVersion, e));
            return true;
        }
        return false;
    }
}
