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
        }
    }

    private void apply(final long version, final ReservaCreadaEvent event) {
        final var reserva = Reserva.builder()
                .id(event.getAggregateRootId())
                .version(version)
                .representacion(event.getRepresentacion())
                .butacas(event.getButacas())
                .cliente(event.getCliente())
                .build();

        repository.save(reserva);
    }
}
