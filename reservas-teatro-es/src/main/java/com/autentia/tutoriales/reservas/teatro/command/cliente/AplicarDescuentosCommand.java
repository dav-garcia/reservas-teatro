package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

@Value
public class AplicarDescuentosCommand implements Command<Cliente, String> {

    String aggregateRootId;
    UUID enReserva;
    int maximo;

    @Override
    public void execute(final Repository<Cliente, String> repository, final EventPublisher<String> eventPublisher) {
        final var cliente = repository.load(aggregateRootId).orElseThrow();
        final var descuentos = cliente.getDescuentosAplicables(LocalDate.now(), maximo).stream()
                .map(Descuento::getId)
                .collect(Collectors.toList());

        eventPublisher.tryPublish(cliente.getVersion(), new DescuentosAplicadosEvent(aggregateRootId, enReserva, descuentos));
    }
}
