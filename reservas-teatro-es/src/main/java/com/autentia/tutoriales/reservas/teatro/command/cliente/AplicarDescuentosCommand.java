package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.event.cliente.DescuentosAplicadosEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

@Value
public class AplicarDescuentosCommand implements Command<ClienteCommandContext, Cliente, String> {

    String aggregateRootId;
    UUID enReserva;
    int maximo;

    @Override
    public void execute(final ClienteCommandContext context) {
        final var cliente = context.getRepository().load(aggregateRootId).orElseThrow();
        final var descuentos = cliente.getDescuentosAplicables(LocalDate.now(), maximo).stream()
                .map(Descuento::getId)
                .collect(Collectors.toList());

        context.getEventPublisher().tryPublish(cliente.getVersion(),
                new DescuentosAplicadosEvent(aggregateRootId, enReserva, descuentos));
    }
}
