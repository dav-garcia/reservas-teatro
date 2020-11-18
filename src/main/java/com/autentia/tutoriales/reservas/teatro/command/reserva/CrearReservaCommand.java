package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.command.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import lombok.Value;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Value
public class CrearReservaCommand implements Command<Reserva, UUID> {

    UUID representacion;
    Set<Butaca> butacas;

    @Override
    public void execute(UUID id, Repository<Reserva, UUID> repository, EventPublisher<UUID> eventPublisher) {
        if (repository.load(id).isPresent()) {
            throw new CommandNotValidException("Reserva ya existe");
        }

        eventPublisher.tryPublish(id, 0, List.of(new ReservaCreadaEvent(representacion, butacas)));
    }
}
