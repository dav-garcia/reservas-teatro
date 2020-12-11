package com.autentia.tutoriales.reservas.teatro.event;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.util.UUID;

@Value
public class DescuentoConcedidoEvent implements Event<String> {

    String aggregateRootId;
    UUID id;
}
