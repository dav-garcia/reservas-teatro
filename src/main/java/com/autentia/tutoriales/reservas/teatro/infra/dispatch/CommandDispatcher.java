package com.autentia.tutoriales.reservas.teatro.infra.dispatch;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.Command;

/**
 * Despacha comandos a una agregada con diferentes estrategias
 */
public interface CommandDispatcher<T extends AggregateRoot<U>, U> {

    /**
     * Despacha un comando a la instancia de identificador dado
     *
     * @param id Identificador de la instancia
     * @param command Comando a ejecutar
     * @throws CommandNotValidException Si el comando no se puede aplicar a la agregada
     */
    void dispatch(final U id, final Command<T, U> command);
}
