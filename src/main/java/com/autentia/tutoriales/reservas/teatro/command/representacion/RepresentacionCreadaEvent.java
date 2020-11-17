package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.UUID;

@Value
public class RepresentacionCreadaEvent implements Event<Representacion, UUID> {

    ZonedDateTime cuando;
    Sala donde;

    @Override
    public void apply(final UUID id, final long version, final Repository<Representacion, UUID> repository) {
        final var representacion = Representacion.builder()
                .id(id)
                .version(version)
                .cuando(cuando)
                .donde(donde)
                .butacasLibres(new HashSet<>(donde.getButacas()))
                .build();

        repository.save(representacion);
    }
}
