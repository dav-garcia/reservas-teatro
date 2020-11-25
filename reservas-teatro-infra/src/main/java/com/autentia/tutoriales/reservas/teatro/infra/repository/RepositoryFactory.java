package com.autentia.tutoriales.reservas.teatro.infra.repository;

import com.autentia.tutoriales.reservas.teatro.infra.Entity;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepositoryFactory;

@SuppressWarnings("java:S1610")
public abstract class RepositoryFactory {

    private static class InstanceHolder {
        public static final RepositoryFactory INSTANCE = new InMemoryRepositoryFactory();
    }

    public static RepositoryFactory getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static <T extends Entity<U>, U> Repository<T, U> getRepository(final Class<T> type) {
        return getInstance().get(type);
    }

    public abstract <T extends Entity<U>, U> Repository<T, U> get(final Class<T> type);
}
