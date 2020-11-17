package com.autentia.tutoriales.reservas.teatro.infra.dispatch;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.EventSourceId;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

/**
 * Despacha comandos a agregadas con diferentes estrategias
 */
public interface CommandDispatcher {

    /**
     * Registra una agregada para que pueda procesar comandos
     *
     * @param type Clase de la agregada
     * @param repository Su repositorio
     */
    <T extends AggregateRoot<U>, U> void registerAggregateRoot(final Class<T> type, final Repository<T, U> repository);

    /**
     * Despacha un comando a una agregada de identificador dado
     *
     * @param command Comando a ejecutar
     * @param id Identificador de la agregada
     * @throws CommandNotValidException Si el comando no se puede aplicar a la agregada
     */
    <T extends AggregateRoot<U>, U> void dispatch(final Command<T, U> command, final EventSourceId<T, U> id);
}
