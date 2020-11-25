package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.repository.RepositoryFactory;

import java.util.HashSet;
import java.util.UUID;

public class RepresentacionEventConsumer implements EventConsumer<UUID> {

    private final Repository<Representacion, UUID> repository;

    public RepresentacionEventConsumer() {
        repository = RepositoryFactory.getRepository(Representacion.class);
    }

    @Override
    public void consume(long version, Event<UUID> event) {
        if (event instanceof RepresentacionCreadaEvent) {
            apply(version, (RepresentacionCreadaEvent) event);
        } else if (event instanceof ButacasSeleccionadasEvent) {
            apply(version, (ButacasSeleccionadasEvent) event);
        }
    }

    public void apply(final long version, final RepresentacionCreadaEvent event) {
        final var representacion = Representacion.builder()
                .id(event.getAggregateRootId())
                .version(version)
                .cuando(event.getCuando())
                .donde(event.getDonde())
                .butacasLibres(new HashSet<>(event.getDonde().getButacas()))
                .build();

        repository.save(representacion);
    }

    public void apply(final long version, final ButacasSeleccionadasEvent event) {
        final var representacion = repository.load(event.getAggregateRootId()).orElseThrow();

        representacion.setVersion(version);
        representacion.getButacasLibres().removeAll(event.getButacas());

        repository.save(representacion);
    }
}
