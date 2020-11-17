package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventStream;
import lombok.Value;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Value
public class SeleccionarButacasCommand implements Command<Representacion, UUID> {

    Set<Butaca> butacas;

    @Override
    public void execute(UUID id, Repository<Representacion, UUID> repository, EventStream<Representacion, UUID> eventStream) {
        final var representacion = repository.load(id)
                .filter(r -> r.getButacasLibres().containsAll(butacas))
                .orElseThrow(() -> new CommandNotValidException("Representación no existe o las butacas no están libres"));

        eventStream.tryPublish(representacion.getVersion(), List.of(new ButacasSeleccionadasEvent(butacas)));
    }
}
