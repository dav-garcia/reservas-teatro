package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventConsumer;

import java.util.HashSet;
import java.util.UUID;

public class RepresentacionEventConsumer implements EventConsumer<UUID> {

    private final Repository<Representacion, UUID> repository;

    public RepresentacionEventConsumer(final Repository<Representacion, UUID> repository) {
        this.repository = repository;
    }

    @Override
    public void consume(final UUID id, long version, Event event) {
        if (event instanceof RepresentacionCreadaEvent) {
            apply(id, version, (RepresentacionCreadaEvent) event);
        } else if (event instanceof ButacasSeleccionadasEvent) {
            apply(id, version, (ButacasSeleccionadasEvent) event);
        }
    }

    public void apply(final UUID id, final long version, final RepresentacionCreadaEvent event) {
        final var representacion = Representacion.builder()
                .id(id)
                .version(version)
                .cuando(event.getCuando())
                .donde(event.getDonde())
                .butacasLibres(new HashSet<>(event.getDonde().getButacas()))
                .build();

        repository.save(representacion);
    }

    public void apply(final UUID id, final long version, final ButacasSeleccionadasEvent event) {
        final var representacion = repository.load(id).orElseThrow();

        representacion.setVersion(version);
        representacion.getButacasLibres().removeAll(event.getButacas());

        repository.save(representacion);
    }
}
