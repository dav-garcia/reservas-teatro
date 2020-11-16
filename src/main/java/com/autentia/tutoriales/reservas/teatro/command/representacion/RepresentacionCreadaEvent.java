package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.UUID;

@Value
public class RepresentacionCreadaEvent implements Event<Representacion, UUID> {

    UUID rootId;
    ZonedDateTime cuando;
    Sala donde;

    @Override
    public void apply(final Repository<Representacion, UUID> repository, final long version) {
        final var representacion = Representacion.builder()
                .id(rootId)
                .version(version)
                .cuando(cuando)
                .donde(donde)
                .butacasLibres(new HashSet<>(donde.getButacas()))
                .build();

        repository.save(representacion);
    }
}
