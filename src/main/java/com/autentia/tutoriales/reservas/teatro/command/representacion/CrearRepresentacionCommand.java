package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventStream;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Value
public class CrearRepresentacionCommand implements Command<Representacion, UUID> {

    ZonedDateTime cuando;
    Sala donde;

    @Override
    public void execute(UUID id, Repository<Representacion, UUID> repository, EventStream<Representacion, UUID> eventStream) {
        if (repository.load(id).isPresent()) {
            throw new CommandNotValidException("Representación ya existe");
        }

        eventStream.tryPublish(0, List.of(new RepresentacionCreadaEvent(cuando, donde)));
    }
}
