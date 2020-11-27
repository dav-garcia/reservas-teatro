package com.autentia.tutoriales.reservas.teatro.command.pago;

import com.autentia.tutoriales.reservas.teatro.event.pago.PagoConfirmadoEvent;
import com.autentia.tutoriales.reservas.teatro.event.pago.PagoPropuestoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.UUID;

public class PagoEventConsumer implements EventConsumer<UUID> {

    private final Repository<Pago, UUID> repository;

    public PagoEventConsumer(final Repository<Pago, UUID> repository) {
        this.repository = repository;
    }

    @Override
    public void consume(final long version, final Event<UUID> event) {
        if (event instanceof PagoPropuestoEvent) {
            apply(version, (PagoPropuestoEvent) event);
        } else if (event instanceof PagoConfirmadoEvent) {
            apply(version, (PagoConfirmadoEvent) event);
        }
    }

    private void apply(final long version, final PagoPropuestoEvent event) {
        final var pago = Pago.builder()
                .id(event.getAggregateRootId())
                .version(version)
                .reserva(event.getReserva())
                .cliente(event.getCliente())
                .conceptos(event.getConceptos())
                .codigoPago(event.getCodigoPago())
                .build();

        repository.save(pago);
    }

    @SuppressWarnings("java:S1172")
    private void apply(final long version, final PagoConfirmadoEvent event) {
        repository.delete(event.getAggregateRootId());
    }
}
