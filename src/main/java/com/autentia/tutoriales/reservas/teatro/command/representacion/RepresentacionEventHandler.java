package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.Event;
import com.autentia.tutoriales.reservas.teatro.EventHandler;
import com.autentia.tutoriales.reservas.teatro.repository.Repository;

import java.util.UUID;

public class RepresentacionEventHandler implements EventHandler<UUID> {

    private final Repository<Representacion, UUID> repository;

    public RepresentacionEventHandler(Repository<Representacion, UUID> repository) {
        this.repository = repository;
    }

    @Override
    public void apply(final long version, final Event<UUID> event) {
        final var representacion = repository.load(event.getRootId()).orElseThrow();
        representacion.setVersion(version);
        if (event instanceof ButacasSeleccionadasEvent) {
            final var butacasSeleccionadasEvent = (ButacasSeleccionadasEvent) event;
            representacion.getButacasLibres().removeAll(butacasSeleccionadasEvent.getButacas());
        }
    }
}
