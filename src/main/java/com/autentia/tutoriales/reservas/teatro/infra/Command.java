package com.autentia.tutoriales.reservas.teatro.infra;

import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;

/**
 * Un comando para cambiar el estado de una agregada
 */
public interface Command<T extends AggregateRoot<U>, U> {

    /**
     * Valida el comando contra el estado actual de la agregada y, si es correcto,
     * lo ejecuta publicando eventos al stream
     * <p>
     * Si la publicación de eventos falla con {@link InconsistentStateException}, el
     * {@link CommandDispatcher} puede intentar repetir el comando, así que su ejecución
     * debe ser idempotente
     *
     * @param id Identificador de la agregada
     * @param repository Repositorio de la agregada
     * @param eventPublisher Stream para publicar eventos
     * @throws CommandNotValidException Si el comando no es válido
     */
    void execute(final U id, final Repository<T, U> repository, final EventPublisher<U> eventPublisher);
}
