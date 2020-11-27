package com.autentia.tutoriales.reservas.teatro.configuration;

import com.autentia.tutoriales.reservas.teatro.command.cliente.Cliente;
import com.autentia.tutoriales.reservas.teatro.command.cliente.ClienteCommandSupport;
import com.autentia.tutoriales.reservas.teatro.command.cliente.ClienteEventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.occ.OccCommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
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
    public ClienteEventConsumer clienteEventConsumer(final Repository<Cliente, String> clienteRepository) {
        return new ClienteEventConsumer(clienteRepository);
    }

    @Bean
    public EventPublisher<String> clientePublisher(final ClienteEventConsumer clienteEventConsumer) {
        final var result = new InMemoryEventPublisher<String>();
        result.registerEventConsumer(clienteEventConsumer);
        return result;
    }

    @Bean
    public CommandDispatcher<String> clienteDispatcher(final Repository<Cliente, String> clienteRepository,
                                                       final EventPublisher<String> clientePublisher) {
        ClienteCommandSupport.setup(clienteRepository);
        return new OccCommandDispatcher<>(clientePublisher);
    }
}
