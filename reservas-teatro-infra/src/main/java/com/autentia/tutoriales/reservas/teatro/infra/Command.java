package com.autentia.tutoriales.reservas.teatro.infra;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandContext;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;

/**
 * Un comando para cambiar el estado de una agregada
 */
public interface Command<C extends CommandContext<T, U>, T extends AggregateRoot<U>, U> {

    /**
     * @return identificador de la instancia de agregada sobre la que se ejecuta el comando
     */
    U getAggregateRootId();

    /**
     * Valida el comando contra el estado actual del modelo y, si es correcto,
     * lo ejecuta publicando eventos al stream
     * <p>
     * Si la publicación de eventos falla con {@link InconsistentStateException}, el
     * {@link CommandDispatcher} puede intentar repetir el comando, así que su ejecución
     * debe ser idempotente
     *
     * @param context Contexto de ejecución que da acceso a los servicios requeridos por el comando
     * @throws CommandNotValidException Si el comando no es válido
     */
    void execute(final C context);
}
