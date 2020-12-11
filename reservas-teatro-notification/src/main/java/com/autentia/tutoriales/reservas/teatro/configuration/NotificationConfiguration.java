package com.autentia.tutoriales.reservas.teatro.configuration;

import com.autentia.tutoriales.reservas.teatro.Cliente;
import com.autentia.tutoriales.reservas.teatro.DescuentoService;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.event.inmemory.InMemoryEventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.DummyPlatformTransactionManager;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class NotificationConfiguration {

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DummyPlatformTransactionManager();
    }

    @Bean
    public Repository<Cliente, String> clienteRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public InMemoryEventPublisher<String> clientePublisher() {
        return new InMemoryEventPublisher<>();
    }

    @Bean
    public DescuentoService descuentosService(
            final Repository<Cliente, String> clienteRepository,
            final EventPublisher<String> clientePublisher) {
        return new DescuentoService(clienteRepository, clientePublisher);
    }
}
