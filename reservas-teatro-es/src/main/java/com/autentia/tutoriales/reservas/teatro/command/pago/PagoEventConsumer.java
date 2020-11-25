package com.autentia.tutoriales.reservas.teatro.command.pago;

import com.autentia.tutoriales.reservas.teatro.event.pago.PagoPropuestoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.RepositoryFactory;

import java.util.UUID;

public class PagoEventConsumer implements EventConsumer<UUID> {

    private final Repository<Pago, UUID> repository;

    public PagoEventConsumer() {
        repository = RepositoryFactory.getRepository(Pago.class);
    }

    @Override
    public void consume(final long version, final Event<UUID> event) {
        if (event instanceof PagoPropuestoEvent) {
            apply(version, (PagoPropuestoEvent) event);
        }
    }

    private void apply(final long version, final PagoPropuestoEvent event) {
        final var pago = Pago.builder()
                .id(event.getAggregateRootId())
                .version(version)
                .reserva(event.getReserva())
                .cliente(event.getCliente())
                .conceptos(event.getConceptos())
                .idPasarelaPago(event.getIdPasarelaPago())
                .build();

        repository.save(pago);
    }
}
