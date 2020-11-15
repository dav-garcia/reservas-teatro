package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.NoSideEffectsCommand;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.List;

@Value
public class CrearRepresentacionCommand implements NoSideEffectsCommand<Representacion> {

    ZonedDateTime cuando;
    Sala donde;

    @Override
    public boolean isValid(Representacion root) {
        return true;
    }

    @Override
    public List<Event> execute(Representacion root) {
        return List.of(new RepresentacionCreadaEvent(root.getId(), cuando, donde));
    }
}
