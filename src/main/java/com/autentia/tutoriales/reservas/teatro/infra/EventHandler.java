package com.autentia.tutoriales.reservas.teatro.infra;

public interface EventHandler {

    void apply(final long version, final Event event);
}
