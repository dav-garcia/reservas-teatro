package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class ButacasSeleccionadasEvent implements Event<Representacion, UUID> {

    UUID rootId;
    Set<Butaca> butacas;

    @Override
    public void apply(final Repository<Representacion, UUID> repository, final long version) {
        final var representacion = repository.load(rootId).orElseThrow();

        representacion.setVersion(version);
        representacion.getButacasLibres().removeAll(butacas);

        repository.save(representacion);
    }
}
