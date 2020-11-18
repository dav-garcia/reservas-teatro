package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventConsumer;

import java.util.UUID;

public class ReservaEventConsumer implements EventConsumer<UUID> {

    private final Repository<Reserva, UUID> repository;

    public ReservaEventConsumer(final Repository<Reserva, UUID> repository) {
        this.repository = repository;
    }

    @Override
    public void consume(final UUID id, final long version, final Event event) {
        if (event instanceof ReservaCreadaEvent) {
            apply(id, version, (ReservaCreadaEvent) event);
        }
    }

    private void apply(final UUID id, final long version, final ReservaCreadaEvent event) {
        final var reserva = Reserva.builder()
                .id(id)
                .version(version)
                .representacion(event.getRepresentacion())
                .butacas(event.getButacas())
                .build();

        repository.save(reserva);
    }
}
