package com.autentia.tutoriales.reservas.teatro.infra.event;

import com.autentia.tutoriales.reservas.teatro.infra.Event;

/**
 * Consumidor de eventos de una agregada
 */
public interface EventConsumer<U> {

    /**
     * Método invocado cuando se ha publicado un nuevo evento en el stream de esta agregada
     *
     * @param version Número de versión que debe guardarse
     * @param event Evento publicado
     */
    void consume(final long version, final Event<U> event);
}
