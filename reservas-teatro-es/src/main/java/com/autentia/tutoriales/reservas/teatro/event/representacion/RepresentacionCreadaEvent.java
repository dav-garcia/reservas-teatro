package com.autentia.tutoriales.reservas.teatro.event.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

@Value
public class RepresentacionCreadaEvent implements Event<UUID> {

    UUID aggregateRootId;
    ZonedDateTime cuando;
    Sala donde;
}
