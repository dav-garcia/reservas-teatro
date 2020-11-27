package com.autentia.tutoriales.reservas.teatro.configuration;

import com.autentia.tutoriales.reservas.teatro.command.cliente.Cliente;
import com.autentia.tutoriales.reservas.teatro.command.pago.Pago;
import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.event.inmemory.InMemoryEventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.saga.ReservaTeatroSaga;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class SagaConfiguration {

    @Bean
    public ReservaTeatroSaga reservaTeatroSaga(final CommandDispatcher<UUID> representacionDispatcher,
                                               final CommandDispatcher<UUID> reservaDispatcher,
                                               final CommandDispatcher<String> clienteDispatcher,
                                               final CommandDispatcher<UUID> pagoDispatcher,
                                               final Repository<Reserva, UUID> reservaRepository,
                                               final Repository<Cliente, String> clienteRepository,
                                               final Repository<Pago, UUID> pagoRepository,
                                               final EventPublisher<UUID> representacionPublisher,
                                               final EventPublisher<UUID> reservaPublisher,
                                               final EventPublisher<String> clientePublisher,
                                               final EventPublisher<UUID> pagoPublisher) {
        final var result = new ReservaTeatroSaga(
                representacionDispatcher, reservaDispatcher, clienteDispatcher, pagoDispatcher,
                reservaRepository, clienteRepository, pagoRepository);

        ((InMemoryEventPublisher<UUID>) representacionPublisher).registerEventConsumer(result.getRepresentacionEventConsumer());
        ((InMemoryEventPublisher<UUID>) reservaPublisher).registerEventConsumer(result.getReservaEventConsumer());
        ((InMemoryEventPublisher<String>) clientePublisher).registerEventConsumer(result.getClienteEventConsumer());
        ((InMemoryEventPublisher<UUID>) pagoPublisher).registerEventConsumer(result.getPagoEventConsumer());

        return result;
    }
}
