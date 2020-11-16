package com.autentia.tutoriales.reservas.teatro.infra.dispatcher;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.EventSourceId;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

public interface CommandDispatcher {

    <T extends AggregateRoot<U>, U> void registerAggregateRoot(final Class<T> type, final Repository<T, U> repository);
    <T extends AggregateRoot<U>, U> void dispatch(final Command<T, U> command, final EventSourceId<T, U> id);
}
