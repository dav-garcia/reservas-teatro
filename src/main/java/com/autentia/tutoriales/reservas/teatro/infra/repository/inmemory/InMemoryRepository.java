package com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class InMemoryRepository<T extends AggregateRoot<U>, U> implements Repository<T, U> {

    private final Map<U, T> instances;
    private final Random random;

    public InMemoryRepository() {
        instances = new HashMap<>();
        random = new Random();
    }

    @Override
    public void save(T instance) {
        randomPause();
        instances.put(instance.getId(), instance);
    }

    @Override
    public Optional<T> load(U id) {
        randomPause();
        return Optional.ofNullable(instances.get(id));
    }

    @Override
    public void delete(U id) {
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
