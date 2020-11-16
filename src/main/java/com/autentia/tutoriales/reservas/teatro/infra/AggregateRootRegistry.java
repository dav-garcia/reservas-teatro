package com.autentia.tutoriales.reservas.teatro.infra;

import com.autentia.tutoriales.reservas.teatro.error.CommandException;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AggregateRootRegistry {

    private final Map<Class<?>, Repository<?, ?>> registry;

    public AggregateRootRegistry() {
        registry = new HashMap<>();
    }

    public <T extends AggregateRoot<U>, U> void register(Class<T> type, Repository<T, U> repository) {
        registry.put(type, repository);
    }

    @NonNull
    public <T extends AggregateRoot<U>, U> Repository<T, U> getRepository(EventSourceId<T, U> id) {
        return (Repository<T, U>) Optional.ofNullable(registry.get(id.getType()))
                .orElseThrow(() -> new CommandException("Unknown aggregate root " + id.getType().getSimpleName()));
    }

    public <T extends AggregateRoot<U>, U> T getAggregateRoot(EventSourceId<T, U> id) {
        final var repository = getRepository(id);
        return repository.load(id.getId()).orElseGet(() -> repository.create(id.getId()));
    }
}
