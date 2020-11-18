package com.autentia.tutoriales.reservas.teatro.command.cliente;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder")
public class Descuento {

    UUID id;
    int valor;
    LocalDate validoDesde;
    LocalDate validoHasta;
    boolean consumido;
}
