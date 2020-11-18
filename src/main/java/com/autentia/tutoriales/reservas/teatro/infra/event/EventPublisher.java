package com.autentia.tutoriales.reservas.teatro.infra.event;

import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.Event;

/**
 * Publicador de eventos de una agregada
 */
public interface EventPublisher<U> {

    /**
     * Intenta publicar el evento dado en el stream de la agregada
     *
     * @param expectedVersion Versión de la agregada sobre la que deben aplicarse los eventos
     * @param event Evento a aplicar
     * @throws InconsistentStateException Si la última versión no coincide con la versión dada
     */
    void tryPublish(final long expectedVersion, Event<U> event);
}
