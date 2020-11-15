package com.autentia.tutoriales.reservas.teatro.infra.dispatcher;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.Command;

public interface CommandDispatcher {

    <T extends AggregateRoot<?>> void dispatch(final Command<T> command, final T root);
}
