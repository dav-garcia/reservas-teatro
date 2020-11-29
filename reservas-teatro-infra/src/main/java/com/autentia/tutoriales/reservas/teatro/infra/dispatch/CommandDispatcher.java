package com.autentia.tutoriales.reservas.teatro.infra.dispatch;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.Command;

/**
 * Despacha comandos a una agregada con diferentes estrategias
 */
public interface CommandDispatcher<C extends CommandContext<T, U>, T extends AggregateRoot<U>, U> {

    /**
     * Despacha un comando
     *
     * @param command Comando a ejecutar
     * @throws CommandNotValidException Si el comando no se puede aplicar a la agregada
     */
    void dispatch(final Command<C, T, U> command);
}
