package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.command.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class CrearReservaCommand implements Command<Reserva, UUID> {

    UUID aggregateRootId;
    UUID representacion;
    Set<Butaca> butacas;

    @Override
    public void execute(Repository<Reserva, UUID> repository, EventPublisher<UUID> eventPublisher) {
        if (repository.load(aggregateRootId).isPresent()) {
            throw new CommandNotValidException("Reserva ya existe");
        }

        eventPublisher.tryPublish(0L, new ReservaCreadaEvent(aggregateRootId, representacion, butacas));
    }
}
