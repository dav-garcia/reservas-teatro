package com.autentia.tutoriales.reservas.teatro.infra.stream;

import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.Event;

import java.util.List;

/**
 * Stream de eventos de una instancia de agregada
 */
public interface EventStream<T extends AggregateRoot<U>, U> {

    /**
     * Intenta publicar los eventos dados en el stream de la instancia
     *
     * @param expectedVersion Versión de la agregada sobre la que deben aplicarse los eventos
     * @param events Eventos a aplicar
     * @throws InconsistentStateException Si la última versión no coincide con la versión dada
     */
    void tryPublish(final long expectedVersion, final List<Event<T, U>> events);
}
