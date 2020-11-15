package com.autentia.tutoriales.reservas.teatro.infra.dispatcher.syncrhonized;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.dispatcher.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.handler.EventStreamFactory;
import com.autentia.tutoriales.reservas.teatro.infra.handler.EventStreamId;

public class SynchronizedCommandDispatcher implements CommandDispatcher {

    private final EventStreamFactory eventStreamFactory;

    public SynchronizedCommandDispatcher(final EventStreamFactory eventStreamFactory) {
        this.eventStreamFactory = eventStreamFactory;
    }

    @Override
    @SuppressWarnings("java:S3740")
    public synchronized <T extends AggregateRoot<?>> void dispatch(final Command<T> command, final T root) {
        if (command.isValid(root)) {
            final var streamId = new EventStreamId(root.getClass(), root.getId());
            final var stream = eventStreamFactory.getForAggregateRoot(streamId);
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
            final var root = (AggregateRoot) object;
            result += String.format("[%s@%d]", root.getId(), root.getVersion());
        }
        return result;
    }
}
