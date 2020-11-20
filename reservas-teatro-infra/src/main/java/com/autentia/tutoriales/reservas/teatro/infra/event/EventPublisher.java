package com.autentia.tutoriales.reservas.teatro.infra.event;

import com.autentia.tutoriales.reservas.teatro.error.EventException;
import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.Event;

import java.util.List;

/**
 * Publicador de eventos de una agregada
 */
public interface EventPublisher<U> {

    /**
     * Intenta publicar una lista de eventos en el stream de la agregada
     * <p>
     * Lanza excepción si no todos los eventos se refieren a la misma instancia
     *
     * @param expectedVersion Versión de la agregada sobre la que deben aplicarse los eventos
     * @param events Lista de eventos a aplicar
     * @throws InconsistentStateException Si la última versión no coincide con la versión dada
     * @throws EventException Si no todos los eventos afectan a la misma instancia
     */
    void tryPublish(final long expectedVersion, List<Event<U>> events);

    /**
     * Intenta publicar el evento dado en el stream de la agregada
     *
     * @param expectedVersion Versión de la agregada sobre la que debe aplicarse el evento
     * @param event Evento a aplicar
     * @throws InconsistentStateException Si la última versión no coincide con la versión dada
     */
    default void tryPublish(final long expectedVersion, Event<U> event) {
        tryPublish(expectedVersion, List.of(event));
    }
}
