package com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryRepository<T extends AggregateRoot<U>, U> implements Repository<T, U> {

    private final Function<U, T> newInstance;
    private final Map<U, T> instances;

    public InMemoryRepository(Function<U, T> newInstance) {
        this.newInstance = newInstance;
        instances = new HashMap<>();
    }

    @Override
    public T create(final U id) {
        return newInstance.apply(id);
    }

    @Override
    public void save(T root) {
        instances.put(root.getId(), root);
    }

    @Override
    public Optional<T> load(U id) {
        return Optional.ofNullable(instances.get(id));
    }

    @Override
    public void delete(U id) {
        instances.remove(id);
    }
}
