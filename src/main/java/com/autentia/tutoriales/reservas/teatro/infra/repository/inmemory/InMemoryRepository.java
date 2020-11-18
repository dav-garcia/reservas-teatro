package com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryRepository<T extends AggregateRoot<U>, U> implements Repository<T, U> {

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

    private void randomPause() {
        try {
            Thread.sleep(10L + random.nextInt(90));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
