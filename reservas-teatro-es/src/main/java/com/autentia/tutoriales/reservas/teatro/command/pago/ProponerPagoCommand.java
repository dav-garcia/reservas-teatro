package com.autentia.tutoriales.reservas.teatro.command.pago;

import com.autentia.tutoriales.reservas.teatro.adapter.pasarelapago.PasarelaPagoFactory;
import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.pago.PagoPropuestoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.RepositoryFactory;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.List;
import java.util.UUID;

@Value
@RequiredArgsConstructor
public class ProponerPagoCommand implements Command<UUID> {

    UUID aggregateRootId;
    UUID reserva;
    String cliente;
    List<Concepto> conceptos;

    @NonFinal
    String idPasarelaPago;

    @Override
    public void execute(final EventPublisher<UUID> eventPublisher) {
        final var repository = RepositoryFactory.getRepository(Pago.class);
        if (repository.load(aggregateRootId).isPresent()) {
            throw new CommandNotValidException("El pago ya ha sido propuesto");
        }

        if (idPasarelaPago == null) { // Idempotencia con el proveedor externo en caso de repetición
            idPasarelaPago = iniciarPago(); // El comando tiene estado
        }

        eventPublisher.tryPublish(0L, new PagoPropuestoEvent(aggregateRootId, reserva, cliente, conceptos, idPasarelaPago));
    }

    private String iniciarPago() {
        final var descripcion = "Reserva " + reserva;
        final var valor = conceptos.stream()
                .mapToInt(Concepto::getPrecio)
                .sum();

        return PasarelaPagoFactory.getPasarelaPago().iniciarPago(cliente, descripcion, valor);
    }
}
