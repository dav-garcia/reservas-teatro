package com.autentia.tutoriales.reservas.teatro.infra;

import org.springframework.lang.NonNull;

/**
 * Todas las agregadas deben implementar esta interfaz
 */
public interface AggregateRoot<T> {

    @NonNull
    T getId();
    long getVersion();
}
