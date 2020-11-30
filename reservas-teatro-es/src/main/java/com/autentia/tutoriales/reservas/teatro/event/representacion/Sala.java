package com.autentia.tutoriales.reservas.teatro.event.representacion;

import lombok.Value;

import java.util.Set;

@Value
public class Sala {

    String nombre;
    Set<Butaca> butacas;
}
