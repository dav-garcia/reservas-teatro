package com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory;

import com.autentia.tutoriales.reservas.teatro.infra.Entity;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.RepositoryFactory;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepositoryFactory extends RepositoryFactory {

    private final Map<Class<?>, Repository<?, ?>> repositories;

    public InMemoryRepositoryFactory() {
        repositories = new HashMap<>();
    }

    @Override
    public <T extends Entity<U>, U> Repository<T, U> get(final Class<T> type) {
        final var repository = repositories.computeIfAbsent(type, t -> new InMemoryRepository<>());
        return (Repository<T, U>) repository;
    }
}
