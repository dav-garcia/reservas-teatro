package com.autentia.tutoriales.reservas.teatro.infra.event;

import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.Event;

import java.util.List;

/**
 * Publicador de eventos de una agregada
 */
public interface EventPublisher<U> {

    /**
     * Intenta publicar los eventos dados en el stream de la agregada
     *
     * @param id Identificador de la agregada
     * @param expectedVersion Versión de la agregada sobre la que deben aplicarse los eventos
     * @param events Eventos a aplicar
     * @throws InconsistentStateException Si la última versión no coincide con la versión dada
     */
    void tryPublish(final U id, final long expectedVersion, final List<Event> events);
}
