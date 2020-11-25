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
    public <T extends Entity<U>, U> Repository<T, U> getRepository(final Class<T> type) {
        return (Repository<T, U>) repositories.computeIfAbsent(type, t -> new InMemoryRepository<>());
    }
}
