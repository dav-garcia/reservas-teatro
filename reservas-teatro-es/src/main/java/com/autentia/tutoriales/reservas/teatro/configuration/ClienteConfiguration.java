package com.autentia.tutoriales.reservas.teatro.configuration;

import com.autentia.tutoriales.reservas.teatro.command.cliente.Cliente;
import com.autentia.tutoriales.reservas.teatro.command.cliente.ClienteCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.cliente.ClienteEventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.occ.OccCommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.inmemory.InMemoryEventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClienteConfiguration {

    @Bean
    public Repository<Cliente, String> clienteRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public InMemoryEventPublisher<String> clientePublisher() {
        return new InMemoryEventPublisher<>();
    }

    @Bean
    public ClienteEventConsumer clienteEventConsumer(final Repository<Cliente, String> clienteRepository,
                                                     final InMemoryEventPublisher<String> clientePublisher) {
        final var result = new ClienteEventConsumer(clienteRepository);
        clientePublisher.registerEventConsumer(result);
        return result;
    }

    @Bean
    public ClienteCommandContext clienteCommandContext(final Repository<Cliente, String> clienteRepository,
                                                       final InMemoryEventPublisher<String> clientePublisher) {
        return new ClienteCommandContext(clienteRepository, clientePublisher);
    }

    @Bean
    public CommandDispatcher<ClienteCommandContext, Cliente, String> clienteDispatcher(
            final ClienteCommandContext context) {
        return new OccCommandDispatcher<>(context);
    }
}
