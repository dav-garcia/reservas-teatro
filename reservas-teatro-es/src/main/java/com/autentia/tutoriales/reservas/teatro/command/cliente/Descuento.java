package com.autentia.tutoriales.reservas.teatro.command.cliente;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder")
public class Descuento {

    private final UUID id;
    private final String descripcion;
    private final int valor;
    private final LocalDate validoDesde;
    private final LocalDate validoHasta;
    UUID enReserva;
}
