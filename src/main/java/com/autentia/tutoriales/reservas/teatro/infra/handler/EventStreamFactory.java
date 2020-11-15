package com.autentia.tutoriales.reservas.teatro.infra.handler;

import org.springframework.lang.NonNull;

public interface EventStreamFactory {

    @NonNull
    EventStream getForRoot(final EventStreamId<?> id);
}
