package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventPublisher;
import lombok.Value;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Value
public class SeleccionarButacasCommand implements Command<Representacion, UUID> {

    Set<Butaca> butacas;

    @Override
    public void execute(final UUID id, final Repository<Representacion, UUID> repository, final EventPublisher<UUID> eventPublisher) {
        final var representacion = repository.load(id)
                .filter(r -> r.getButacasLibres().containsAll(butacas))
                .orElseThrow(() -> new CommandNotValidException("Representación no existe o las butacas no están libres"));

        eventPublisher.tryPublish(id, representacion.getVersion(), List.of(new ButacasSeleccionadasEvent(butacas)));
    }
}
