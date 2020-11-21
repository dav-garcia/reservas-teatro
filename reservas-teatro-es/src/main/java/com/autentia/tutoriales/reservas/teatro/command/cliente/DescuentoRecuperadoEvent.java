package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.util.UUID;

@Value
public class DescuentoRecuperadoEvent implements Event<String> {

    String aggregateRootId;
    UUID descuento;
}
