package com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<T extends AggregateRoot<U>, U> implements Repository<T, U> {

    private final Map<U, T> instances;

    public InMemoryRepository() {
        instances = new HashMap<>();
    }

    @Override
    public U create(final T root) {
        if (instances.containsKey(root.getId())) {
            throw new IllegalStateException(
                    String.format("Instance %s of type %s already created", root.getId(), root.getClass().getSimpleName()));
        }
        instances.put(root.getId(), root);
        return root.getId();
    }

    @Override
    public Optional<T> load(U id) {
        return Optional.ofNullable(instances.get(id));
    }

    @Override
    public void update(T root) {
        if (!instances.containsKey(root.getId())) {
            throw new IllegalStateException(
                    String.format("Instance %s of type %s does not exist", root.getId(), root.getClass().getSimpleName()));
        }
        instances.put(root.getId(), root);
    }

    @Override
    public void delete(U id) {
        instances.remove(id);
    }
}
