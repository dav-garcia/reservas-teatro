package com.autentia.tutoriales.reservas.teatro.dispatcher.syncrhonized;

import com.autentia.tutoriales.reservas.teatro.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.Command;
import com.autentia.tutoriales.reservas.teatro.dispatcher.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.handler.EventStreamFactory;
import com.autentia.tutoriales.reservas.teatro.handler.EventStreamId;

public class SynchronizedCommandDispatcher implements CommandDispatcher {

    private final EventStreamFactory eventStreamFactory;

    public SynchronizedCommandDispatcher(final EventStreamFactory eventStreamFactory) {
        this.eventStreamFactory = eventStreamFactory;
    }

    @Override
    public synchronized <T extends AggregateRoot<?>> void dispatch(final Command<T> command, final T root) {
        if (command.isValid(root)) {
            final var streamId = new EventStreamId(root.getClass(), root.getId());
            final var stream = eventStreamFactory.getForRoot(streamId);
            final long currentVersion = root.getVersion();
            final long latestVersion = stream.getLatestVersion();

            if (currentVersion < latestVersion) {
                throw new IllegalStateException(
                        String.format("Outdated command for instance %s of type %s: %d vs %d",
                                root.getId(), root.getClass().getSimpleName(), currentVersion, latestVersion));
            } else if (currentVersion > latestVersion) {
                throw new IllegalStateException(
                        String.format("Incomplete projection of instance %s of type %s: %d vs %d",
                                root.getId(), root.getClass().getSimpleName(), currentVersion, latestVersion));
            } else {
                final var events = command.execute(root);
                if (stream.tryPublish(root.getVersion(), events)) {
                    command.committed(root);
                } else {
                    command.rolledBack(root);
                }
            }
        }
    }
}
