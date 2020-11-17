package com.autentia.tutoriales.reservas.teatro.infra.stream;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.EventSourceId;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import org.springframework.lang.NonNull;

/**
 * Factoría de streams de eventos
 */
public interface EventStreamFactory {

    /**
     * Registra una agregada para poder recibir eventos
     *
     * @param type Clase de la agregada
     * @param repository Su repositorio
     */
    <T extends AggregateRoot<U>, U> void registerAggregateRoot(final Class<T> type, final Repository<T, U> repository);

    /**
     * Recupera el stream de eventos de una instancia de agregada
     *
     * @param id Identificador compuesto de la agregada
     * @return Stream de eventos de esa instancia de agregada
     */
    @NonNull
    <T extends AggregateRoot<U>, U> EventStream<T, U> getEventStream(final EventSourceId<T, U> id);
}
