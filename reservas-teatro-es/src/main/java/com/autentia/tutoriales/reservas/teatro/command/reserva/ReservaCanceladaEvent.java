package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.util.UUID;

@Value
public class ReservaCanceladaEvent implements Event<UUID> {

    UUID aggregateRootId;
    boolean abandonada;
}
