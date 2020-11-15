package com.autentia.tutoriales.reservas.teatro.infra.handler;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.EventHandler;
import org.springframework.lang.NonNull;

public interface EventStreamFactory {

    @NonNull
    EventStream getForAggregateRoot(final EventStreamId<?> id);
    void subscribeToType(final Class<? extends AggregateRoot<?>> type, final EventHandler eventHandler);
}
