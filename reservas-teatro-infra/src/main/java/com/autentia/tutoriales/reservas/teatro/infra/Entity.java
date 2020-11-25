package com.autentia.tutoriales.reservas.teatro.infra;

import org.springframework.lang.NonNull;

public interface Entity<T> {

    @NonNull
    T getId();
}
