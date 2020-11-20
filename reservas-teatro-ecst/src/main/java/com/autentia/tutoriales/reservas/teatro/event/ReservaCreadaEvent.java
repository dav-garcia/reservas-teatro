package com.autentia.tutoriales.reservas.teatro.event;

import com.autentia.tutoriales.reservas.teatro.Butaca;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class ReservaCreadaEvent implements Event<UUID> {

    UUID aggregateRootId;
    UUID representacion;
    Set<Butaca> butacas;
    String cliente;
}
