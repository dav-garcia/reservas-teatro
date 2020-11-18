package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class RepresentacionCreadaEvent implements Event {

    ZonedDateTime cuando;
    Sala donde;
}
