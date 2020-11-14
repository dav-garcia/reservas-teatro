package com.autentia.tutoriales.reservas.teatro.repository;

import com.autentia.tutoriales.reservas.teatro.AggregateRoot;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface Repository<T extends AggregateRoot<U>, U> {

    @NonNull
    U create(final T root);
    @NonNull
    Optional<T> load(final U id);
    void update(final T root);
    void delete(final U id);
}
