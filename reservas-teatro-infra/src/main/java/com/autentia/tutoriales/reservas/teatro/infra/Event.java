package com.autentia.tutoriales.reservas.teatro.infra;

/**
 * Un evento que aplica el cambio de estado sobre una agregada
 */
public interface Event<U> {

    /**
     * @return identificador de la instancia de agregada a la que se refiere el evento
     */
    U getAggregateRootId();
}
