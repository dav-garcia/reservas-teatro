package com.autentia.tutoriales.reservas.teatro.infra.dispatch.occ;

import com.autentia.tutoriales.reservas.teatro.error.InconsistentStateException;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandContext;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccCommandDispatcher<C extends CommandContext<T, U>, T extends AggregateRoot<U>, U>
        implements CommandDispatcher<C, T, U> {

    private static final Logger LOG = LoggerFactory.getLogger(OccCommandDispatcher.class);

    private final C context;

    public OccCommandDispatcher(final C context) {
        this.context = context;
    }

    public C getContext() {
        return context;
    }

    @Override
    public void dispatch(final Command<C, T, U> command) {
        var retry = true;
        while (retry) {
            try {
                command.execute(context);
                retry = false;
            } catch (InconsistentStateException e) {
                LOG.warn("Reintentando comando por estado inconsistente", e);
            }
        }
    }
}
