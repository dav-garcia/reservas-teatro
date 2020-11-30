package com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory;

import com.autentia.tutoriales.reservas.teatro.infra.Entity;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InMemoryRepository<T extends Entity<U>, U> implements Repository<T, U> {

    private final ConcurrentMap<U, T> instances;

    public InMemoryRepository() {
        instances = new ConcurrentHashMap<>();
    }

    @Override
    public void save(final T instance) {
        instances.put(instance.getId(), instance);
    }

    @Override
    public Optional<T> load(final U id) {
        return Optional.ofNullable(instances.get(id));
    }

    @Override
    public List<T> find(final Predicate<T> filter) {
        return instances.values().stream().filter(filter).collect(Collectors.toList());
    }

    @Override
    public void delete(final U id) {
        instances.remove(id);
    }
}
