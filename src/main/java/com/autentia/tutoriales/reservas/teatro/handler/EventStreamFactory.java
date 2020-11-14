package com.autentia.tutoriales.reservas.teatro.handler;

import org.springframework.lang.NonNull;

public interface EventStreamFactory {

    @NonNull
    EventStream getForRoot(final EventStreamId<?> id);
}
