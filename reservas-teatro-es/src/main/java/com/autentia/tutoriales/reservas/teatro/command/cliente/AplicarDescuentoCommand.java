package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value
public class AplicarDescuentoCommand implements Command<Cliente, String> {

    String aggregateRootId;
    UUID descuento;

    @Override
    public void execute(final Repository<Cliente, String> repository, final EventPublisher<String> eventPublisher) {
        final var cliente = repository.load(aggregateRootId).orElseThrow();
        if (cliente.getDescuentos().stream().noneMatch(d -> d.getId().equals(descuento)
                && ahoraEntreFechas(d.getValidoDesde(), d.getValidoHasta())
                && !d.consumido)) {
            throw new CommandNotValidException("No se puede aplicar el descuento");
        }

        eventPublisher.tryPublish(cliente.getVersion(), new DescuentoAplicadoEvent(aggregateRootId, descuento));
    }

    private boolean ahoraEntreFechas(final LocalDate desde, final LocalDate hasta) {
        final var ahora = LocalDate.now();
        return desde.compareTo(ahora) <= 0 && hasta.compareTo(ahora) >= 0;
    }
}
