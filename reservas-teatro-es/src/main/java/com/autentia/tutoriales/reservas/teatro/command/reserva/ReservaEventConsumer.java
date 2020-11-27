package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaAbandonadaEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaCanceladaEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaConfirmadaEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaCreadaEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaPagadaEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

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
            applyEstado(version, event.getAggregateRootId(), Reserva.Estado.CONFIRMADA);
        } else if (event instanceof ReservaAbandonadaEvent) {
            applyEstado(version, event.getAggregateRootId(), Reserva.Estado.ABANDONADA);
        } else if (event instanceof ReservaCanceladaEvent) {
            applyEstado(version, event.getAggregateRootId(), Reserva.Estado.CANCELADA);
        } else if (event instanceof ReservaPagadaEvent) {
            applyEstado(version, event.getAggregateRootId(), Reserva.Estado.PAGADA);
        }
    }

    private void apply(final long version, final ReservaCreadaEvent event) {
        final var reserva = Reserva.builder()
                .id(event.getAggregateRootId())
                .version(version)
                .representacion(event.getRepresentacion())
                .butacas(event.getButacas())
                .cliente(event.getCliente())
                .estado(Reserva.Estado.CREADA)
                .build();

        repository.save(reserva);
    }

    private void applyEstado(final long version, final UUID id, final Reserva.Estado estado) {
        final var reserva = repository.load(id).orElseThrow();

        reserva.setVersion(version);
        reserva.setEstado(estado);

        repository.save(reserva);
    }
}
