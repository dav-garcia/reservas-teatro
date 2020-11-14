package com.autentia.tutoriales.reservas.teatro;

public interface EventHandler<T> {

    void apply(final long version, final Event<T> event);
}
