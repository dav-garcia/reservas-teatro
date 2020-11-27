package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.representacion.ButacasLiberadasEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class LiberarButacasCommand implements Command<UUID> {

    UUID aggregateRootId;
    Set<Butaca> butacas;

    @Override
    public void execute(EventPublisher<UUID> eventPublisher) {
        final var representacion = RepresentacionCommandSupport.getRepository().load(aggregateRootId)
                .orElseThrow(() -> new CommandNotValidException("Representación no existe"));

        eventPublisher.tryPublish(representacion.getVersion(), new ButacasLiberadasEvent(aggregateRootId, butacas));

    }
}
