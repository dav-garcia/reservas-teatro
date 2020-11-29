package com.autentia.tutoriales.reservas.teatro.saga;

import com.autentia.tutoriales.reservas.teatro.command.cliente.Cliente;
import com.autentia.tutoriales.reservas.teatro.command.cliente.ClienteCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.cliente.RecuperarDescuentosCommand;
import com.autentia.tutoriales.reservas.teatro.command.representacion.LiberarButacasCommand;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Representacion;
import com.autentia.tutoriales.reservas.teatro.command.representacion.RepresentacionCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.reserva.PagarReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaCommandContext;
import com.autentia.tutoriales.reservas.teatro.event.pago.PagoAnuladoEvent;
import com.autentia.tutoriales.reservas.teatro.event.pago.PagoConfirmadoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.Objects;
import java.util.UUID;

public class PagoSaga implements EventConsumer<UUID> {

    private final Repository<EstadoSaga, UUID> repository;
    private final CommandDispatcher<RepresentacionCommandContext, Representacion, UUID> representacionDispatcher;
    private final CommandDispatcher<ReservaCommandContext, Reserva, UUID> reservaDispatcher;
    private final CommandDispatcher<ClienteCommandContext, Cliente, String> clienteDispatcher;

    public PagoSaga(final Repository<EstadoSaga, UUID> repository,
                    final CommandDispatcher<RepresentacionCommandContext, Representacion, UUID> representacionDispatcher,
                    final CommandDispatcher<ReservaCommandContext, Reserva, UUID> reservaDispatcher,
                    final CommandDispatcher<ClienteCommandContext, Cliente, String> clienteDispatcher) {
        this.repository = repository;
        this.representacionDispatcher = representacionDispatcher;
        this.reservaDispatcher = reservaDispatcher;
        this.clienteDispatcher = clienteDispatcher;
    }

    @Override
    public void consume(final long version, final Event<UUID> event) {
        if (event instanceof PagoConfirmadoEvent) {
            process((PagoConfirmadoEvent) event);
        } else if (event instanceof PagoAnuladoEvent) {
            process((PagoAnuladoEvent) event);
        }
    }

    private void process(final PagoConfirmadoEvent event) {
        final var estado = repository.find(e -> Objects.equals(e.getPago(), event.getAggregateRootId()))
                .get(0);

        reservaDispatcher.dispatch(new PagarReservaCommand(estado.getId()));
    }

    private void process(final PagoAnuladoEvent event) {
        final var estado = repository.find(e -> Objects.equals(e.getPago(), event.getAggregateRootId()))
                .get(0);

        recuperarDescuentos(estado);
        liberarButacas(estado);
    }

    private void recuperarDescuentos(final EstadoSaga estado) {
        clienteDispatcher.dispatch(new RecuperarDescuentosCommand(estado.getCliente(), estado.getId()));
    }

    private void liberarButacas(final EstadoSaga estadoSaga) {
        representacionDispatcher.dispatch(new LiberarButacasCommand(estadoSaga.getRepresentacion(), estadoSaga.getButacas()));
    }
}
