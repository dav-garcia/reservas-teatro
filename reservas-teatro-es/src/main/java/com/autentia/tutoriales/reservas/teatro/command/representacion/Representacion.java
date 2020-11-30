package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.event.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder")
public class Representacion implements AggregateRoot<UUID> {

    private final UUID id;
    private long version;
    private final Set<Butaca> butacasLibres;
}
