package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

@Value
public class CrearRepresentacionCommand implements Command<Representacion, UUID> {

    UUID aggregateRootId;
    ZonedDateTime cuando;
    Sala donde;

    @Override
    public void execute(final Repository<Representacion, UUID> repository, final EventPublisher<UUID> eventPublisher) {
        if (repository.load(aggregateRootId).isPresent()) {
            throw new CommandNotValidException("Representación ya existe");
        }

        eventPublisher.tryPublish(0, new RepresentacionCreadaEvent(aggregateRootId, cuando, donde));
    }
}
