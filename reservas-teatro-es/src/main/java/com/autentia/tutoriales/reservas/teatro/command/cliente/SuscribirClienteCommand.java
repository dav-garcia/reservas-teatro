package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Value
public class SuscribirClienteCommand implements Command<Cliente, String> {

    private static final String DESCRIPCION_DESCUENTO = "Descuento por fidelización";
    private static final int VALOR_DESCUENTO = 10;
    private static final int VALIDEZ_DESCUENTO = 30;

    String aggregateRootId;
    String nombre;

    @Override
    public void execute(final Repository<Cliente, String> repository, final EventPublisher<String> eventPublisher) {
        final var clienteOpcional = repository.load(aggregateRootId);
        final boolean suscrito = clienteOpcional.map(Cliente::isSuscrito).orElse(false);
        final long version = clienteOpcional.map(Cliente::getVersion).orElse(0L);

        if (suscrito) {
            throw new CommandNotValidException("El cliente ya está suscrito");
        }

        eventPublisher.tryPublish(version, List.of(
                new ClienteSuscritoEvent(aggregateRootId, nombre),
                new DescuentoConcedidoEvent(aggregateRootId, UUID.randomUUID(),
                        DESCRIPCION_DESCUENTO, VALOR_DESCUENTO,
                        LocalDate.now(), LocalDate.now().plusDays(VALIDEZ_DESCUENTO))));
    }
}
