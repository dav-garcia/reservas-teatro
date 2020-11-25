package com.autentia.tutoriales.reservas.teatro.event.cliente;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class DescuentosRecuperadosEvent implements Event<String> {

    String aggregateRootId;
    UUID deReserva;
    List<UUID> descuentos;
}
