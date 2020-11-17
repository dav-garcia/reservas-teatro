package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class ButacasSeleccionadasEvent implements Event<Representacion, UUID> {

    Set<Butaca> butacas;

    @Override
    public void apply(final UUID id, final long version, final Repository<Representacion, UUID> repository) {
        final var representacion = repository.load(id).orElseThrow();

        representacion.setVersion(version);
        representacion.getButacasLibres().removeAll(butacas);

        repository.save(representacion);
    }
}
