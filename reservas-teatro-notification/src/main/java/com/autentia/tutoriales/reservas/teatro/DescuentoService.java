package com.autentia.tutoriales.reservas.teatro;

import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.DescuentoConcedidoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Transactional
public class DescuentoService {

    private final Repository<Cliente, String> clienteRepository;
    private final EventPublisher<String> clientePublisher;

    public DescuentoService(final Repository<Cliente, String> clienteRepository, final EventPublisher<String> clientePublisher) {
        this.clienteRepository = clienteRepository;
        this.clientePublisher = clientePublisher;
    }

    public UUID concederDescuento(final String email, final String descripcion, final int valor,
                                  final LocalDate validoDesde, final LocalDate validoHasta) {
        final var idDescuento = UUID.randomUUID();
        final var cliente = clienteRepository.load(email)
                .orElseThrow(() -> new CommandNotValidException("El cliente no existe"));
        final var descuento = Descuento.builder()
                .id(idDescuento)
                .descripcion(descripcion)
                .valor(valor)
                .validoDesde(validoDesde)
                .validoHasta(validoHasta)
                .build();

        cliente.getDescuentos().add(descuento);
        clienteRepository.save(cliente);
        clientePublisher.tryPublish(cliente.getVersion(), new DescuentoConcedidoEvent(email, idDescuento));

        return idDescuento;
    }

    public Optional<Descuento> getDescuento(final String email, final UUID idDescuento) {
        final var cliente = clienteRepository.load(email)
                .orElseThrow(() -> new CommandNotValidException("El cliente no existe"));
        return cliente.getDescuentos().stream()
                .filter(d -> d.getId().equals(idDescuento))
                .findAny();
    }
}
