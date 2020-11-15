package com.autentia.tutoriales.reservas.teatro.command.representacion;

import lombok.Value;

import java.util.Set;

@Value
public class Sala {

    String nombre;
    Set<Butaca> butacas;
}
