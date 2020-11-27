package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.command.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaCreadaEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class CrearReservaCommand implements Command<UUID> {

    UUID aggregateRootId;
    UUID representacion;
    Set<Butaca> butacas;
    String cliente;

    @Override
    public void execute(EventPublisher<UUID> eventPublisher) {
        if (ReservaCommandSupport.getRepository().load(aggregateRootId).isPresent()) {
            throw new CommandNotValidException("Reserva ya existe");
        }

        eventPublisher.tryPublish(0L, new ReservaCreadaEvent(aggregateRootId, representacion, butacas, cliente));
    }
}
