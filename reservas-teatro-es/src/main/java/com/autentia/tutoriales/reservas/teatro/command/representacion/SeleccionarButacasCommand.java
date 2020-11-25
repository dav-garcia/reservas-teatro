package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.RepositoryFactory;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class SeleccionarButacasCommand implements Command<Representacion, UUID> {

    UUID aggregateRootId;
    UUID paraReserva;
    Set<Butaca> butacas;
    String email;

    @Override
    public void execute(final EventPublisher<UUID> eventPublisher) {
        final var repository = RepositoryFactory.getRepository(Representacion.class);
        final var representacion = repository.load(aggregateRootId)
                .filter(r -> r.getButacasLibres().containsAll(butacas))
                .orElseThrow(() -> new CommandNotValidException("Representación no existe o las butacas no están libres"));

        eventPublisher.tryPublish(representacion.getVersion(), new ButacasSeleccionadasEvent(aggregateRootId, paraReserva, butacas, email));
    }
}
