package com.autentia.tutoriales.reservas.teatro.infra.repository;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface Repository<T extends AggregateRoot<U>, U> {

    @NonNull
    T create(final U id);
    @NonNull
    Optional<T> load(final U id);
    void save(final T root);
    void delete(final U id);
}
