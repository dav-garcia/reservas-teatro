package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.EventHandler;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.UUID;

public class RepresentacionEventHandler implements EventHandler {

    private final Repository<Representacion, UUID> repository;

    public RepresentacionEventHandler(Repository<Representacion, UUID> repository) {
        this.repository = repository;
    }

    @Override
    public void apply(final long version, final Event event) {
        if (event instanceof ButacasSeleccionadasEvent) {
            final var butacasSeleccionadasEvent = (ButacasSeleccionadasEvent) event;
            final var representacion = repository.load(butacasSeleccionadasEvent.getRootId()).orElseThrow();

            representacion.setVersion(version);
            representacion.getButacasLibres().removeAll(butacasSeleccionadasEvent.getButacas());
        }
    }
}
