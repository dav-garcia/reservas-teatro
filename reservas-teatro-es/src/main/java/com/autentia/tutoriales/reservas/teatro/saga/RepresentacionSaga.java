package com.autentia.tutoriales.reservas.teatro.saga;

import com.autentia.tutoriales.reservas.teatro.command.cliente.Cliente;
import com.autentia.tutoriales.reservas.teatro.command.cliente.ClienteCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.cliente.RegistrarEmailCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.CrearReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaCommandContext;
import com.autentia.tutoriales.reservas.teatro.event.representacion.ButacasSeleccionadasEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.ArrayList;
import java.util.UUID;

public class RepresentacionSaga implements EventConsumer<UUID> {

    private final Repository<EstadoSaga, UUID> repository;
    private final CommandDispatcher<ReservaCommandContext, Reserva, UUID> reservaDispatcher;
    private final CommandDispatcher<ClienteCommandContext, Cliente, String> clienteDispatcher;

    public RepresentacionSaga(final Repository<EstadoSaga, UUID> repository,
                              final CommandDispatcher<ReservaCommandContext, Reserva, UUID> reservaDispatcher,
                              final CommandDispatcher<ClienteCommandContext, Cliente, String> clienteDispatcher) {
        this.repository = repository;
        this.reservaDispatcher = reservaDispatcher;
        this.clienteDispatcher = clienteDispatcher;
    }

    @Override
    public void consume(final long version, final Event<UUID> event) {
        if (event instanceof ButacasSeleccionadasEvent) {
            process((ButacasSeleccionadasEvent) event);
        }
    }

    private void process(final ButacasSeleccionadasEvent event) {
        crearEstadoSaga(event);

        clienteDispatcher.dispatch(new RegistrarEmailCommand(event.getEmail()));
        reservaDispatcher.dispatch(new CrearReservaCommand(event.getParaReserva(),
                event.getAggregateRootId(), event.getButacas(), event.getEmail()));
    }

    private void crearEstadoSaga(final ButacasSeleccionadasEvent event) {
        repository.save(EstadoSaga.builder()
                .id(event.getParaReserva())
                .representacion(event.getAggregateRootId())
                .cliente(event.getEmail())
                .butacas(event.getButacas())
                .descuentos(new ArrayList<>(2))
                .build());
    }
}
