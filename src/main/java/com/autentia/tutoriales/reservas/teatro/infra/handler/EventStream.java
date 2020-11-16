package com.autentia.tutoriales.reservas.teatro.infra.handler;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import org.springframework.lang.NonNull;

import java.util.List;

public interface EventStream<T extends AggregateRoot<U>, U> {

    @NonNull
    long getLatestVersion();
    boolean tryPublish(final long currentVersion, final List<Event<T, U>> events);
}
