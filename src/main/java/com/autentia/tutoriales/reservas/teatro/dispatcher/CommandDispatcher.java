package com.autentia.tutoriales.reservas.teatro.dispatcher;

import com.autentia.tutoriales.reservas.teatro.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.Command;

public interface CommandDispatcher {

    <T extends AggregateRoot<?>> void dispatch(final Command<T> command, final T root);
}
