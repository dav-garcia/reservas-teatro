package com.autentia.tutoriales.reservas.teatro.event.reserva;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.util.UUID;

@Value
public class ReservaPagadaEvent implements Event<UUID> {

    UUID aggregateRootId;
}
