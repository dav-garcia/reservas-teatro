package com.autentia.tutoriales.reservas.teatro.command.pago;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class ProponerPago implements Command<Pago, UUID> {

    UUID aggregateRootId;
    UUID reserva;
    String cliente;
    List<Concepto> conceptos;

    @Override
    public void execute(final Repository<Pago, UUID> repository, final EventPublisher<UUID> eventPublisher) {
        if (repository.load(aggregateRootId).isPresent()) {
            throw new CommandNotValidException("El pago ya ha sido propuesto");
        }

        eventPublisher.tryPublish(0L, new PagoPropuestoEvent(aggregateRootId, reserva, cliente, conceptos));
    }
}
