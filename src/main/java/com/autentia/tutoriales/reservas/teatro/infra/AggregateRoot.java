package com.autentia.tutoriales.reservas.teatro.infra;

import org.springframework.lang.NonNull;

public interface AggregateRoot<T> {

    @NonNull
    T getId();
    long getVersion();
}
