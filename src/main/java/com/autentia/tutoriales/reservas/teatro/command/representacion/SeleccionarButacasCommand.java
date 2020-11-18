package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class SeleccionarButacasCommand implements Command<Representacion, UUID> {

    UUID aggregateRootId;
    Set<Butaca> butacas;
    String email;

    @Override
    public void execute(final Repository<Representacion, UUID> repository, final EventPublisher<UUID> eventPublisher) {
        final var representacion = repository.load(aggregateRootId)
                .filter(r -> r.getButacasLibres().containsAll(butacas))
                .orElseThrow(() -> new CommandNotValidException("Representación no existe o las butacas no están libres"));

        eventPublisher.tryPublish(representacion.getVersion(), new ButacasSeleccionadasEvent(aggregateRootId, butacas, email));
    }
}
