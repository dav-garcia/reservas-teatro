package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder")
public class Reserva implements AggregateRoot<UUID> {

    public enum Estado {
        CREADA,
        CONFIRMADA,
        PAGADA,
        ABANDONADA,
        CANCELADA
    }

    private final UUID id;
    private long version;
    private Estado estado;
}
