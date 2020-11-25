package com.autentia.tutoriales.reservas.teatro.event.cliente;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

@Value
public class ClienteSuscritoEvent implements Event<String> {

    String aggregateRootId;
    String nombre;
}
