package com.autentia.tutoriales.reservas.teatro.event.representacion;

import com.autentia.tutoriales.reservas.teatro.command.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class ButacasSeleccionadasEvent implements Event<UUID> {

    UUID aggregateRootId;
    UUID paraReserva;
    Set<Butaca> butacas;
    String email;
}
