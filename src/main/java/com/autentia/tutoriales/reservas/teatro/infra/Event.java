package com.autentia.tutoriales.reservas.teatro.infra;

import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

/**
 * Un evento que aplica el cambio de estado sobre una agregada
 */
public interface Event<T extends AggregateRoot<U>, U> {

    /**
     * Aplica el evento a la agregada de identificador dado
     *
     * @param id Identificador de la agregada
     * @param version Número de versión que debe guardarse
     * @param repository Repositorio de la agregada
     */
    void apply(final U id, final long version, final Repository<T, U> repository);
}
