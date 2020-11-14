package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.AggregateRoot;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder(builderClassName = "Builder")
public class Representacion implements AggregateRoot<UUID> {

    private UUID id;
    @Setter
    private long version;
    private Set<Butaca> butacasLibres;

}
