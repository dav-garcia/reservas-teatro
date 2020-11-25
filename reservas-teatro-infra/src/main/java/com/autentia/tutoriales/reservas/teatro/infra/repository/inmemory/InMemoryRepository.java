package com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory;

import com.autentia.tutoriales.reservas.teatro.infra.Entity;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InMemoryRepository<T extends Entity<U>, U> implements Repository<T, U> {

    private final ConcurrentMap<U, T> instances;
    private final Random random;

    public InMemoryRepository() {
        instances = new ConcurrentHashMap<>();
        random = new Random();
    }

    @Override
    public void save(final T instance) {
        randomPause();
        instances.put(instance.getId(), instance);
    }

    @Override
    public Optional<T> load(final U id) {
        randomPause();
        return Optional.ofNullable(instances.get(id));
    }

    @Override
    public void delete(final U id) {
        randomPause();
        instances.remove(id);
    }

    @Override
    public List<T> find(final Predicate<T> filter) {
        return instances.values().stream().filter(filter).collect(Collectors.toList());
    }

    private void randomPause() {
        try {
            Thread.sleep(10L + random.nextInt(90));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
