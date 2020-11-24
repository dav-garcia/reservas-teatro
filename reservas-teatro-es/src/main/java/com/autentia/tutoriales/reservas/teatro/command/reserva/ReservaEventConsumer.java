package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;

import java.util.UUID;

public class ReservaEventConsumer implements EventConsumer<UUID> {

    private final Repository<Reserva, UUID> repository;

    public ReservaEventConsumer(final Repository<Reserva, UUID> repository) {
        this.repository = repository;
    }

    @Override
    public void consume(final long version, final Event<UUID> event) {
        if (event instanceof ReservaCreadaEvent) {
            apply(version, (ReservaCreadaEvent) event);
        } else if (event instanceof ReservaConfirmadaEvent) {
            apply(version, (ReservaConfirmadaEvent) event);
        } else if (event instanceof ReservaCanceladaEvent) {
            apply(version, (ReservaCanceladaEvent) event);
        }
    }

    private void apply(final long version, final ReservaCreadaEvent event) {
        final var reserva = Reserva.builder()
                .id(event.getAggregateRootId())
                .version(version)
                .butacas(event.getButacas())
                .cliente(event.getCliente())
                .build();

        repository.save(reserva);
    }

    private void apply(final long version, final ReservaConfirmadaEvent event) {
        final var reserva = repository.load(event.getAggregateRootId()).orElseThrow();

        reserva.setVersion(version);
        reserva.setConfirmada(true);

        repository.save(reserva);
    }

    @SuppressWarnings("java:S1172")
    private void apply(final long version, final ReservaCanceladaEvent event) {
        repository.delete(event.getAggregateRootId());
    }
}
