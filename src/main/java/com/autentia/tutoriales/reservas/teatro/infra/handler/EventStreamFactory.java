package com.autentia.tutoriales.reservas.teatro.infra.handler;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.EventSourceId;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import org.springframework.lang.NonNull;

public interface EventStreamFactory {

    <T extends AggregateRoot<U>, U> void registerAggregateRoot(final Class<T> type, final Repository<T, U> repository);

    @NonNull
    <T extends AggregateRoot<U>, U> EventStream<T, U> get(final EventSourceId<T, U> id);
}
