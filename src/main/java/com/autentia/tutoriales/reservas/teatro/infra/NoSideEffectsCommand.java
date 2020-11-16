package com.autentia.tutoriales.reservas.teatro.infra;

public interface NoSideEffectsCommand<T extends AggregateRoot<U>, U> extends Command<T, U> {

    default void committed(final T root) {
        // Do nothing
    }

    default void rolledBack(final T root) {
        // Do nothing
    }
}
