package com.autentia.tutoriales.reservas.teatro;

public interface EventHandler {

    void apply(final long version, final Event event);
}
