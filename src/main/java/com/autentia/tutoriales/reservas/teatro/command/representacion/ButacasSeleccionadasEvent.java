package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.util.Set;

@Value
public class ButacasSeleccionadasEvent implements Event {

    Set<Butaca> butacas;
}
