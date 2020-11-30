package com.autentia.tutoriales.reservas.teatro.saga;

import com.autentia.tutoriales.reservas.teatro.command.cliente.AplicarDescuentosCommand;
import com.autentia.tutoriales.reservas.teatro.command.cliente.Cliente;
import com.autentia.tutoriales.reservas.teatro.command.cliente.ClienteCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.cliente.RecuperarDescuentosCommand;
import com.autentia.tutoriales.reservas.teatro.command.pago.AnularPagoCommand;
import com.autentia.tutoriales.reservas.teatro.command.pago.Pago;
import com.autentia.tutoriales.reservas.teatro.command.pago.PagoCommandContext;
import com.autentia.tutoriales.reservas.teatro.event.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.command.representacion.LiberarButacasCommand;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Representacion;
import com.autentia.tutoriales.reservas.teatro.command.representacion.RepresentacionCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.reserva.AbandonarReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaCommandContext;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaAbandonadaEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaCanceladaEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaConfirmadaEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaCreadaEvent;
import com.autentia.tutoriales.reservas.teatro.event.reserva.ReservaPagadaEvent;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.task.TaskScheduler;

import java.util.UUID;

public class ReservaSaga implements EventConsumer<UUID> {

    private static final String TASK_TYPE = "timeout";
    private static final int DEFAULT_TIMEOUT = 10 * 60;

    private final Repository<EstadoProceso, UUID> repository;
    private final CommandDispatcher<RepresentacionCommandContext, Representacion, UUID> representacionDispatcher;
    private final CommandDispatcher<ReservaCommandContext, Reserva, UUID> reservaDispatcher;
    private final CommandDispatcher<ClienteCommandContext, Cliente, String> clienteDispatcher;
    private final CommandDispatcher<PagoCommandContext, Pago, UUID> pagoDispatcher;
    private final TaskScheduler taskScheduler;

    private int timeout;

    public ReservaSaga(final Repository<EstadoProceso, UUID> repository,
                       final CommandDispatcher<RepresentacionCommandContext, Representacion, UUID> representacionDispatcher,
                       final CommandDispatcher<ReservaCommandContext, Reserva, UUID> reservaDispatcher,
                       final CommandDispatcher<ClienteCommandContext, Cliente, String> clienteDispatcher,
                       final CommandDispatcher<PagoCommandContext, Pago, UUID> pagoDispatcher,
                       final TaskScheduler taskScheduler) {
        this.repository = repository;
        this.representacionDispatcher = representacionDispatcher;
        this.reservaDispatcher = reservaDispatcher;
        this.clienteDispatcher = clienteDispatcher;
        this.pagoDispatcher = pagoDispatcher;
        this.taskScheduler = taskScheduler;

        timeout = DEFAULT_TIMEOUT;
    }

    public void setTimeout(final Integer timeout) {
        this.timeout = timeout == null ? DEFAULT_TIMEOUT : timeout;
    }

    @Override
    public void consume(final long version, final Event<UUID> event) {
        if (event instanceof ReservaCreadaEvent) {
            process((ReservaCreadaEvent) event);
        } else if (event instanceof ReservaConfirmadaEvent) {
            process((ReservaConfirmadaEvent) event);
        } else if (event instanceof ReservaAbandonadaEvent) {
            process((ReservaAbandonadaEvent) event);
        } else if (event instanceof ReservaCanceladaEvent) {
            process((ReservaCanceladaEvent) event);
        } else if (event instanceof ReservaPagadaEvent) {
            process((ReservaPagadaEvent) event);
        }
    }

    private void process(final ReservaCreadaEvent event) {
        final var id= event.getAggregateRootId();

        taskScheduler.scheduleTask(TASK_TYPE, id, () -> reservaDispatcher.dispatch(new AbandonarReservaCommand(id)), timeout);
    }

    private void process(final ReservaConfirmadaEvent event) {
        final var estado = repository.load(event.getAggregateRootId()).orElseThrow();
        final var maximo = estado.getButacas().stream()
                .mapToInt(Butaca::getPrecio)
                .sum();

        clienteDispatcher.dispatch(new AplicarDescuentosCommand(estado.getCliente(), estado.getId(), maximo));
    }

    private void process(final ReservaAbandonadaEvent event) {
        final var estado = repository.load(event.getAggregateRootId()).orElseThrow();

        if (!anularPago(estado)) {
            recuperarDescuentos(estado);
            liberarButacas(estado);
        }
    }

    private void process(final ReservaCanceladaEvent event) {
        final var estado = repository.load(event.getAggregateRootId()).orElseThrow();

        taskScheduler.cancelTask(TASK_TYPE, event.getAggregateRootId());

        if (!anularPago(estado)) {
            recuperarDescuentos(estado);
            liberarButacas(estado);
        }
    }

    private void process(final ReservaPagadaEvent event) {
        taskScheduler.cancelTask(TASK_TYPE, event.getAggregateRootId());

        repository.delete(event.getAggregateRootId());
    }

    private boolean anularPago(final EstadoProceso estado) {
        if (estado.getPago() == null) {
            return false;
        }

        pagoDispatcher.dispatch(new AnularPagoCommand(estado.getPago()));
        return true;
    }

    private void recuperarDescuentos(final EstadoProceso estado) {
        clienteDispatcher.dispatch(new RecuperarDescuentosCommand(estado.getCliente(), estado.getId()));
    }

    private void liberarButacas(final EstadoProceso estado) {
        representacionDispatcher.dispatch(new LiberarButacasCommand(estado.getRepresentacion(), estado.getButacas()));
    }
}
