package com.autentia.tutoriales.reservas.teatro.event.representacion;

import lombok.Value;

@Value
public class Butaca {

    String fila;
    int silla;
    int precio;
}
