package com.autentia.tutoriales.reservas.teatro.saga;

import com.autentia.tutoriales.reservas.teatro.command.cliente.Descuento;
import com.autentia.tutoriales.reservas.teatro.event.pago.Concepto;
import com.autentia.tutoriales.reservas.teatro.command.pago.Pago;
import com.autentia.tutoriales.reservas.teatro.command.pago.PagoCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.pago.ProponerPagoIdempotentCommand;
import com.autentia.tutoriales.reservas.teatro.event.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.event.cliente.DescuentoConcedidoEvent;
import com.autentia.tutoriales.reservas.teatro.event.cliente.DescuentosAplicadosEvent;
import com.autentia.tutoriales.reservas.teatro.event.cliente.DescuentosRecuperadosEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.ArrayList;
import java.util.UUID;

public class ClienteSaga implements EventConsumer<String> {

    private final Repository<EstadoProceso, UUID> repository;
    private final Repository<Descuento, UUID> descuentoRepository;
    private final CommandDispatcher<PagoCommandContext, Pago, UUID> pagoDispatcher;

    public ClienteSaga(final Repository<EstadoProceso, UUID> repository,
                       final Repository<Descuento, UUID> descuentoRepository,
                       final CommandDispatcher<PagoCommandContext, Pago, UUID> pagoDispatcher) {
        this.repository = repository;
        this.descuentoRepository = descuentoRepository;
        this.pagoDispatcher = pagoDispatcher;
    }

    @Override
    public void consume(final long version, final Event<String> event) {
        if (event instanceof DescuentoConcedidoEvent) {
            process((DescuentoConcedidoEvent) event);
        } else if (event instanceof DescuentosAplicadosEvent) {
            process((DescuentosAplicadosEvent) event);
        } else if (event instanceof DescuentosRecuperadosEvent) {
            process((DescuentosRecuperadosEvent) event);
        }
    }

    private void process(final DescuentoConcedidoEvent event) {
        final var descuento = Descuento.builder()
                .id(event.getId())
                .descripcion(event.getDescripcion())
                .valor(event.getValor())
                .validoDesde(event.getValidoDesde())
                .validoHasta(event.getValidoHasta())
                .build();

        descuentoRepository.save(descuento);
    }

    private void process(final DescuentosAplicadosEvent event) {
        final var estado = repository.load(event.getEnReserva()).orElseThrow();

        final var conceptos = new ArrayList<Concepto>();

        for (final Butaca butaca : estado.getButacas()) {
            conceptos.add(new Concepto("Butaca " + butaca.getFila() + butaca.getSilla(), butaca.getPrecio()));
        }
        for (final Descuento descuento : descuentoRepository.find(d -> event.getDescuentos().contains(d.getId()))) {
            descuento.setEnReserva(event.getEnReserva());
            descuentoRepository.save(descuento);

            conceptos.add(new Concepto(descuento.getDescripcion(), -descuento.getValor()));
        }

        pagoDispatcher.dispatch(new ProponerPagoIdempotentCommand(
                UUID.randomUUID(), event.getEnReserva(), event.getAggregateRootId(), conceptos));
    }

    private void process(final DescuentosRecuperadosEvent event) {
        for (final Descuento descuento : descuentoRepository.find(d -> event.getDescuentos().contains(d.getId()))) {
            descuento.setEnReserva(null);
            descuentoRepository.save(descuento);
        }
    }
}
