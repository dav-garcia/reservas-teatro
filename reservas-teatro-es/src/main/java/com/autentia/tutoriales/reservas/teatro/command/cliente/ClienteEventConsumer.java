package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.ArrayList;

public class ClienteEventConsumer implements EventConsumer<String> {

    private final Repository<Cliente, String> repository;

    public ClienteEventConsumer(Repository<Cliente, String> repository) {
        this.repository = repository;
    }

    @Override
    public void consume(final long version, final Event<String> event) {
        if (event instanceof EmailRegistradoEvent) {
            apply(version, (EmailRegistradoEvent) event);
        } else if (event instanceof ClienteSuscritoEvent) {
            apply(version, (ClienteSuscritoEvent) event);
        } else if (event instanceof DescuentoConcedidoEvent) {
            apply(version, (DescuentoConcedidoEvent) event);
        }
    }

    private void apply(final long version, final EmailRegistradoEvent event) {
        final var cliente = Cliente.builder()
                .id(event.getAggregateRootId())
                .version(version)
                .suscrito(false)
                .descuentos(new ArrayList<>())
                .build();

        repository.save(cliente);
    }

    private void apply(final long version, final ClienteSuscritoEvent event) {
        final var cliente = repository.load(event.getAggregateRootId())
                .orElseGet(() -> Cliente.builder()
                        .id(event.getAggregateRootId())
                        .descuentos(new ArrayList<>())
                        .build());

        cliente.setVersion(version);
        cliente.setSuscrito(true);
        cliente.setNombre(event.getNombre());

        repository.save(cliente);
    }

    private void apply(final long version, final DescuentoConcedidoEvent event) {
        final var cliente = repository.load(event.getAggregateRootId()).orElseThrow();

        if (cliente.getDescuentos().stream().noneMatch(d -> d.getId().equals(event.getId()))) { // Idempotencia
            cliente.setVersion(version);
            cliente.getDescuentos().add(Descuento.builder()
                    .id(event.getId())
                    .valor(event.getValor())
                    .validoDesde(event.getValidoDesde())
                    .validoHasta(event.getValidoHasta())
                    .consumido(false)
                    .build());

            repository.save(cliente);
        }
    }
}
