package com.autentia.tutoriales.reservas.teatro.infra;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Identifica una instancia de una agregada
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventSourceId<T extends AggregateRoot<U>, U> {

    Class<T> type;
    U value;

    public static <T extends AggregateRoot<U>, U> EventSourceId<T, U> of(final Class<T> type, U id) {
        return new EventSourceId<>(type, id);
    }
}
