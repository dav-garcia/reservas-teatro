package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.EventHandler;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.HashSet;
import java.util.UUID;

public class RepresentacionEventHandler implements EventHandler {

    private final Repository<Representacion, UUID> repository;

    public RepresentacionEventHandler(Repository<Representacion, UUID> repository) {
        this.repository = repository;
    }

    @Override
    public void apply(final long version, final Event event) {
        if (event instanceof RepresentacionCreadaEvent) {
            crear(version, (RepresentacionCreadaEvent) event);
        }
        if (event instanceof ButacasSeleccionadasEvent) {
            seleccionar(version, (ButacasSeleccionadasEvent) event);
        }
    }

    private void crear(long version, RepresentacionCreadaEvent event) {
        final var representacion = Representacion.builder()
                .id(event.getRootId())
                .version(version)
                .cuando(event.getCuando())
                .donde(event.getDonde())
                .butacasLibres(new HashSet<>(event.getDonde().getButacas()))
                .build();

        repository.create(representacion);
    }

    private void seleccionar(long version, ButacasSeleccionadasEvent event) {
        final var representacion = repository.load(event.getRootId()).orElseThrow();

        representacion.setVersion(version);
        representacion.getButacasLibres().removeAll(event.getButacas());

        repository.update(representacion);
    }
}
