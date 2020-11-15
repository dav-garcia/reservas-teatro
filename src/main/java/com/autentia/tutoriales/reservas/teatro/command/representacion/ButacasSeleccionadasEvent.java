package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class ButacasSeleccionadasEvent implements Event {

    UUID rootId;
    Set<Butaca> butacas;
}
