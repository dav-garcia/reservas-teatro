package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.event.cliente.DescuentosRecuperadosEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import lombok.Value;

import java.util.UUID;
import java.util.stream.Collectors;

@Value
public class RecuperarDescuentosCommand implements Command<ClienteCommandContext, Cliente, String> {

    String aggregateRootId;
    UUID deReserva;

    @Override
    public void execute(final ClienteCommandContext context) {
        final var cliente = context.getRepository().load(aggregateRootId).orElseThrow();
        final var descuentos = cliente.getDescuentosAplicados(deReserva).stream()
                .map(Descuento::getId)
                .collect(Collectors.toList());

        context.getEventPublisher().tryPublish(cliente.getVersion(),
                new DescuentosRecuperadosEvent(aggregateRootId, deReserva, descuentos));
    }
}
