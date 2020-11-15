package com.autentia.tutoriales.reservas.teatro.infra;

import org.springframework.lang.NonNull;

import java.util.List;

public interface Command<T extends AggregateRoot<?>> {

    boolean isValid(final T root);
    @NonNull
    List<Event> execute(final T root);
    void committed(final T root);
    void rolledBack(final T root);
}
