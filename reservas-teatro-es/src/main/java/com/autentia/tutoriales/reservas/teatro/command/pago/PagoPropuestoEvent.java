package com.autentia.tutoriales.reservas.teatro.command.pago;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class PagoPropuestoEvent implements Event<UUID> {

    UUID aggregateRootId;
    UUID reserva;
    String cliente;
    List<Concepto> conceptos;
}
