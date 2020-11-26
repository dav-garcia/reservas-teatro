package com.autentia.tutoriales.reservas.teatro.event.pago;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.util.UUID;

@Value
public class PagoConfirmadoEvent implements Event<UUID> {

    UUID aggregateRootId;
}
