package com.autentia.tutoriales.reservas.teatro.infra;

/**
 * Todas las agregadas deben implementar esta interfaz
 */
public interface AggregateRoot<T> extends Entity<T> {

    long getVersion();
}
