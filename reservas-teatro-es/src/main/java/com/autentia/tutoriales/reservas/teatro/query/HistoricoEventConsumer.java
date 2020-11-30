package com.autentia.tutoriales.reservas.teatro.query;

import com.autentia.tutoriales.reservas.teatro.event.cliente.ClienteSuscritoEvent;
import com.autentia.tutoriales.reservas.teatro.event.cliente.DescuentoConcedidoEvent;
import com.autentia.tutoriales.reservas.teatro.event.cliente.DescuentosAplicadosEvent;
import com.autentia.tutoriales.reservas.teatro.event.cliente.DescuentosRecuperadosEvent;
import com.autentia.tutoriales.reservas.teatro.event.pago.PagoPropuestoEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaAbandonadaEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaCanceladaEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaConfirmadaEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaCreadaEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaPagadaEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class HistoricoEventConsumer implements EventConsumer<Object> {

    private final Repository<Historico, String> historicoRepository;
    private final Map<Class<?>, Consumer<Event<?>>> consumers;

    public HistoricoEventConsumer(final Repository<Historico, String> historicoRepository) {
        this.historicoRepository = historicoRepository;
        this.consumers = buildConsumers();
    }

    private Map<Class<?>, Consumer<Event<?>>> buildConsumers() {
        return Map.of(
                ReservaCreadaEvent.class, e -> apply((ReservaCreadaEvent) e),
                ReservaConfirmadaEvent.class, e ->
                        applyEstado(((ReservaConfirmadaEvent) e).getAggregateRootId(), Historico.Reserva.Estado.CONFIRMADA),
                ReservaAbandonadaEvent.class, e ->
                        applyEstado(((ReservaAbandonadaEvent) e).getAggregateRootId(), Historico.Reserva.Estado.ABANDONADA),
                ReservaCanceladaEvent.class, e ->
                        applyEstado(((ReservaCanceladaEvent) e).getAggregateRootId(), Historico.Reserva.Estado.CANCELADA),
                ReservaPagadaEvent.class, e ->
                        applyEstado(((ReservaPagadaEvent) e).getAggregateRootId(), Historico.Reserva.Estado.PAGADA),
                // EmailRegistradoEvent no es necesario
                ClienteSuscritoEvent.class, e -> apply((ClienteSuscritoEvent) e),
                DescuentoConcedidoEvent.class, e -> apply((DescuentoConcedidoEvent) e),
                DescuentosAplicadosEvent.class, e -> apply((DescuentosAplicadosEvent) e),
                DescuentosRecuperadosEvent.class, e -> apply((DescuentosRecuperadosEvent) e),
                PagoPropuestoEvent.class, e -> apply((PagoPropuestoEvent) e)
                // PagoConfirmadoEvent y PagoAnuladoEvent no afectan directamente al histórico
        );
    }

    private void apply(final ReservaCreadaEvent event) {
        final var historico = historicoRepository.load(event.getCliente())
                .orElseGet(() -> Historico.builder()
                        .id(event.getCliente())
                        .descuentos(new ArrayList<>(2))
                        .reservas(new HashMap<>(2))
                        .build());
        historico.getReservas().put(event.getAggregateRootId(), Historico.Reserva.builder()
                .id(event.getAggregateRootId())
                .butacas(event.getButacas())
                .descuentos(new ArrayList<>(2))
                .conceptos(new ArrayList<>(4))
                .estado(Historico.Reserva.Estado.CREADA)
                .build());
        historicoRepository.save(historico);
    }

    private void applyEstado(final UUID idReserva, final Historico.Reserva.Estado estado) {
        final var historico = historicoRepository.find(h -> h.getReservas().containsKey(idReserva)).get(0);
        historico.getReserva(idReserva).setEstado(estado);
        historicoRepository.save(historico);
    }

    private void apply(final ClienteSuscritoEvent event) {
        final var historico = historicoRepository.load(event.getAggregateRootId())
                .orElseGet(() -> Historico.builder()
                        .id(event.getAggregateRootId())
                        .descuentos(new ArrayList<>(2))
                        .reservas(new HashMap<>(2))
                        .build());
        historico.setNombre(event.getNombre());
        historico.setSuscrito(true);
        historicoRepository.save(historico);
    }

    private void apply(final DescuentoConcedidoEvent event) {
        final var historico = historicoRepository.load(event.getAggregateRootId()).orElseThrow();
        historico.getDescuentos().add(new Historico.Descuento(
                event.getId(), event.getDescripcion(), event.getValor(),
                event.getValidoDesde(), event.getValidoHasta()));
        historicoRepository.save(historico);
    }

    private void apply(final DescuentosAplicadosEvent event) {
        final var historico = historicoRepository.load(event.getAggregateRootId()).orElseThrow();
        historico.aplicarDescuentos(event.getEnReserva(), event.getDescuentos());
        historicoRepository.save(historico);
    }

    private void apply(final DescuentosRecuperadosEvent event) {
        final var historico = historicoRepository.load(event.getAggregateRootId()).orElseThrow();
        historico.recuperarDescuentos(event.getDeReserva(), event.getDescuentos());
        historicoRepository.save(historico);
    }

    private void apply(final PagoPropuestoEvent event) {
        final var historico = historicoRepository.load(event.getCliente()).orElseThrow();
        final var reserva = historico.getReserva(event.getReserva());
        reserva.setPago(event.getAggregateRootId());
        reserva.getConceptos().addAll(event.getConceptos());
        historicoRepository.save(historico);
    }

    @Override
    public void consume(final long version, final Event<Object> event) {
        final var consumer = consumers.get(event.getClass());
        if (consumer != null) {
            consumer.accept(event);
        }
    }
}
