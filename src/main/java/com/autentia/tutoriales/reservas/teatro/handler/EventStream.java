package com.autentia.tutoriales.reservas.teatro.handler;

import com.autentia.tutoriales.reservas.teatro.Event;
import org.springframework.lang.NonNull;

import java.util.List;

public interface EventStream<T> {

    @NonNull
    long getLatestVersion();
    boolean tryPublish(final long currentVersion, final List<Event<T>> events);
}
