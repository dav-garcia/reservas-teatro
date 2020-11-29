package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.representacion.ButacasSeleccionadasEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class SeleccionarButacasCommand implements Command<RepresentacionCommandContext, Representacion, UUID> {

    UUID aggregateRootId;
    UUID paraReserva;
    Set<Butaca> butacas;
    String email;

    @Override
    public void execute(final RepresentacionCommandContext context) {
        final var representacion = context.getRepository().load(aggregateRootId)
                .filter(r -> r.getButacasLibres().containsAll(butacas))
                .orElseThrow(() -> new CommandNotValidException("Representación no existe o las butacas no están libres"));

        context.getEventPublisher().tryPublish(representacion.getVersion(),
                new ButacasSeleccionadasEvent(aggregateRootId, paraReserva, butacas, email));
    }
}
