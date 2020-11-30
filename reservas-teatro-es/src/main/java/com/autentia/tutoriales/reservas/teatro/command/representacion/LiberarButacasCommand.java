package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.event.representacion.ButacasLiberadasEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class LiberarButacasCommand implements Command<RepresentacionCommandContext, Representacion, UUID> {

    UUID aggregateRootId;
    Set<Butaca> butacas;

    @Override
    public void execute(final RepresentacionCommandContext context) {
        final var representacion = context.getRepository().load(aggregateRootId)
                .orElseThrow(() -> new CommandNotValidException("Representación no existe"));

        context.getEventPublisher().tryPublish(representacion.getVersion(),
                new ButacasLiberadasEvent(aggregateRootId, butacas));
    }
}
