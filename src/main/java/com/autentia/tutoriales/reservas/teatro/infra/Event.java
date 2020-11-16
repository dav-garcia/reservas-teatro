package com.autentia.tutoriales.reservas.teatro.infra;

import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import org.springframework.lang.NonNull;

public interface Event<T extends AggregateRoot<U>, U> {

    @NonNull
    U getRootId();
    void apply(final Repository<T, U> repository, final long version);
}
