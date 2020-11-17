package com.autentia.tutoriales.reservas.teatro.infra;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventStream;

/**
 * Un comando para cambiar el estado de una agregada
 */
public interface Command<T extends AggregateRoot<U>, U> {

    /**
     * Valida el comando contra el estado actual de la agregada y, si es correcto,
     * lo ejecuta publicando eventos al stream
     *
     * @param id Identificador de la agregada
     * @param repository Repositorio de la agregada
     * @param eventStream Stream para publicar eventos
     * @throws CommandNotValidException Si el comando no es válido
     */
    void execute(final U id, final Repository<T, U> repository, final EventStream<T, U> eventStream);
}
