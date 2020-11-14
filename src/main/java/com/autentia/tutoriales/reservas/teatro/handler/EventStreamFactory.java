package com.autentia.tutoriales.reservas.teatro.handler;

import org.springframework.lang.NonNull;

public interface EventStreamFactory {

    @NonNull
    <T> EventStream<T> getForRoot(final EventStreamId<T> id);
}
