package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.representacion.RepresentacionCreadaEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

@Value
public class CrearRepresentacionCommand implements Command<UUID> {

    UUID aggregateRootId;
    ZonedDateTime cuando;
    Sala donde;

    @Override
    public void execute(final EventPublisher<UUID> eventPublisher) {
        if (RepresentacionCommandSupport.getRepository().load(aggregateRootId).isPresent()) {
            throw new CommandNotValidException("Representación ya existe");
        }

        eventPublisher.tryPublish(0L, new RepresentacionCreadaEvent(aggregateRootId, cuando, donde));
    }
}
