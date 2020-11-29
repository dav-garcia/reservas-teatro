package com.autentia.tutoriales.reservas.teatro.configuration;

import com.autentia.tutoriales.reservas.teatro.command.cliente.Cliente;
import com.autentia.tutoriales.reservas.teatro.command.cliente.ClienteCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.cliente.Descuento;
import com.autentia.tutoriales.reservas.teatro.command.pago.Pago;
import com.autentia.tutoriales.reservas.teatro.command.pago.PagoCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Representacion;
import com.autentia.tutoriales.reservas.teatro.command.representacion.RepresentacionCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaCommandContext;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.inmemory.InMemoryEventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepository;
import com.autentia.tutoriales.reservas.teatro.infra.task.TaskScheduler;
import com.autentia.tutoriales.reservas.teatro.saga.ClienteSaga;
import com.autentia.tutoriales.reservas.teatro.saga.EstadoSaga;
import com.autentia.tutoriales.reservas.teatro.saga.PagoSaga;
import com.autentia.tutoriales.reservas.teatro.saga.RepresentacionSaga;
import com.autentia.tutoriales.reservas.teatro.saga.ReservaSaga;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class SagaConfiguration {

    @Bean
    public TaskScheduler taskScheduler() {
        return new TaskScheduler();
    }

    @Bean
    public Repository<EstadoSaga, UUID> estadoSagaRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public Repository<Descuento, UUID> descuentoRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public RepresentacionSaga representacionSaga(
            final Repository<EstadoSaga, UUID> repository,
            final CommandDispatcher<ReservaCommandContext, Reserva, UUID> reservaDispatcher,
            final CommandDispatcher<ClienteCommandContext, Cliente, String> clienteDispatcher,
            final InMemoryEventPublisher<UUID> representacionPublisher) {
        final var result = new RepresentacionSaga(repository, reservaDispatcher, clienteDispatcher);
        representacionPublisher.registerEventConsumer(result);
        return result;
    }

    @Bean
    public ReservaSaga reservaSaga(
            final Repository<EstadoSaga, UUID> repository,
            final CommandDispatcher<RepresentacionCommandContext, Representacion, UUID> representacionDispatcher,
            final CommandDispatcher<ReservaCommandContext, Reserva, UUID> reservaDispatcher,
            final CommandDispatcher<ClienteCommandContext, Cliente, String> clienteDispatcher,
            final CommandDispatcher<PagoCommandContext, Pago, UUID> pagoDispatcher,
            final TaskScheduler taskScheduler,
            final InMemoryEventPublisher<UUID> reservaPublisher) {
        final var result = new ReservaSaga(repository,
                representacionDispatcher, reservaDispatcher, clienteDispatcher, pagoDispatcher, taskScheduler);
        reservaPublisher.registerEventConsumer(result);
        return result;
    }

    @Bean
    public ClienteSaga clienteSaga(
            final Repository<EstadoSaga, UUID> repository,
            final Repository<Descuento, UUID> descuentoRepository,
            final CommandDispatcher<PagoCommandContext, Pago, UUID> pagoDispatcher,
            final InMemoryEventPublisher<String> clientePublisher) {
        final var result = new ClienteSaga(repository, descuentoRepository, pagoDispatcher);
        clientePublisher.registerEventConsumer(result);
        return result;
    }

    @Bean
    public PagoSaga pagoSaga(
            final Repository<EstadoSaga, UUID> repository,
            final CommandDispatcher<RepresentacionCommandContext, Representacion, UUID> representacionDispatcher,
            final CommandDispatcher<ReservaCommandContext, Reserva, UUID> reservaDispatcher,
            final CommandDispatcher<ClienteCommandContext, Cliente, String> clienteDispatcher,
            final InMemoryEventPublisher<UUID> pagoPublisher) {
        final var result = new PagoSaga(repository, representacionDispatcher, reservaDispatcher, clienteDispatcher);
        pagoPublisher.registerEventConsumer(result);
        return result;
    }
}
