package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.event.cliente.ClienteSuscritoEvent;
import com.autentia.tutoriales.reservas.teatro.event.cliente.DescuentoConcedidoEvent;
import com.autentia.tutoriales.reservas.teatro.event.cliente.DescuentosAplicadosEvent;
import com.autentia.tutoriales.reservas.teatro.event.cliente.DescuentosRecuperadosEvent;
import com.autentia.tutoriales.reservas.teatro.event.cliente.EmailRegistradoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.ArrayList;

public class ClienteEventConsumer implements EventConsumer<String> {

    private final Repository<Cliente, String> repository;

    public ClienteEventConsumer(final Repository<Cliente, String> repository) {
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
        } else if (event instanceof DescuentosAplicadosEvent) {
            apply(version, (DescuentosAplicadosEvent) event);
        } else if (event instanceof DescuentosRecuperadosEvent) {
            apply(version, (DescuentosRecuperadosEvent) event);
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

        repository.save(cliente);
    }

    private void apply(final long version, final DescuentoConcedidoEvent event) {
        final var cliente = repository.load(event.getAggregateRootId()).orElseThrow();

        if (cliente.getDescuentos().stream().noneMatch(d -> d.getId().equals(event.getId()))) { // Idempotencia
            cliente.setVersion(version);
            cliente.getDescuentos().add(Descuento.builder()
                    .id(event.getId())
                    .descripcion(event.getDescripcion())
                    .valor(event.getValor())
                    .validoDesde(event.getValidoDesde())
                    .validoHasta(event.getValidoHasta())
                    .build());

            repository.save(cliente);
        }
    }

    private void apply(final long version, final DescuentosAplicadosEvent event) {
        final var cliente = repository.load(event.getAggregateRootId()).orElseThrow();

        cliente.setVersion(version);
        cliente.getDescuentos().stream()
                .filter(d -> event.getDescuentos().contains(d.getId()))
                .forEach(d -> d.setEnReserva(event.getEnReserva()));

        repository.save(cliente);
    }

    private void apply(final long version, final DescuentosRecuperadosEvent event) {
        final var cliente = repository.load(event.getAggregateRootId()).orElseThrow();

        cliente.setVersion(version);
        cliente.getDescuentos().stream()
                .filter(d -> event.getDescuentos().contains(d.getId()))
                .forEach(d -> d.setEnReserva(null));

        repository.save(cliente);
    }
}
