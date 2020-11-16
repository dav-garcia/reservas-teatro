package com.autentia.tutoriales.reservas.teatro.infra.dispatcher.syncrhonized;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRootRegistry;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.EventSourceId;
import com.autentia.tutoriales.reservas.teatro.infra.dispatcher.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.handler.EventStreamFactory;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

public class SynchronizedCommandDispatcher implements CommandDispatcher {

    private final EventStreamFactory eventStreamFactory;
    private final AggregateRootRegistry registry;

    public SynchronizedCommandDispatcher(final EventStreamFactory eventStreamFactory) {
        this.eventStreamFactory = eventStreamFactory;
        registry = new AggregateRootRegistry();
    }

    @Override
    public <T extends AggregateRoot<U>, U> void registerAggregateRoot(Class<T> type, Repository<T, U> repository) {
        registry.register(type, repository);
    }

    @Override
    public synchronized <T extends AggregateRoot<U>, U> void dispatch(final Command<T, U> command, final EventSourceId<T, U> id) {
        final var root = registry.getAggregateRoot(id);
        if (command.isValid(root)) {
            final var stream = eventStreamFactory.get(id);
            final long currentVersion = root.getVersion();
            final long latestVersion = stream.getLatestVersion();

            if (currentVersion < latestVersion) {
                throw new InconsistentStateException(String.format("Outdated command %s for %s (latestVersion=%d)",
                                buildName(command), buildName(root), latestVersion));
            } else if (currentVersion > latestVersion) {
                throw new InconsistentStateException(String.format("Incomplete projection of %s for %s (latestVersion=%d)",
                                buildName(root), buildName(command), latestVersion));
            } else {
                final var events = command.execute(root);
                if (stream.tryPublish(root.getVersion(), events)) {
                    command.committed(root);
                } else {
                    command.rolledBack(root);
                }
            }
        } else {
            throw new CommandNotValidException(String.format("Rules not satisfied for %s in %s",
                    buildName(command), buildName(root)));
        }
    }

    private String buildName(final Object object) {
        String result = object.getClass().getSimpleName();
        if (object instanceof AggregateRoot) {
            final var root = (AggregateRoot<?>) object;
            result += String.format("[%s@%d]", root.getId(), root.getVersion());
        }
        return result;
    }
}
