package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value
public class DescuentoConcedidoEvent implements Event<String> {

    String aggregateRootId;
    UUID id;
    String descripcion;
    int valor;
    LocalDate validoDesde;
    LocalDate validoHasta;
}
