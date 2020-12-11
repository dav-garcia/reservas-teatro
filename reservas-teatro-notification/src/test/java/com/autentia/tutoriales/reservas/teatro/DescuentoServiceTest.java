package com.autentia.tutoriales.reservas.teatro;

import com.autentia.tutoriales.reservas.teatro.configuration.NotificationConfiguration;
import com.autentia.tutoriales.reservas.teatro.event.DescuentoConcedidoEvent;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.event.inmemory.InMemoryEventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = NotificationConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DescuentoServiceTest {

    @Autowired
    private Repository<Cliente, String> clienteRepository;

    @Autowired
    private InMemoryEventPublisher<String> clientePublisher;

    @Autowired
    private DescuentoService sut;

    @MockBean
    private EventConsumer<String> clienteEventConsumer;

    @Test
    public void givenDescuentoConcedidoThenGetDescuento() {
        final var email = RandomStringUtils.randomAlphabetic(10) + "@email.com";
        final var idOtroDescuento = UUID.randomUUID();
        final var descripcion = RandomStringUtils.randomAlphabetic(10);
        final var valor = 5;
        final var validoDesde = LocalDate.now();
        final var validoHasta = validoDesde.plusDays(10);

        clienteRepository.save(Cliente.builder()
                .id(email)
                .version(1L)
                .descuentos(new ArrayList<>(List.of(Descuento.builder()
                        .id(idOtroDescuento)
                        .build())))
                .build());
        clientePublisher.registerEventConsumer(clienteEventConsumer);

        // Invocado por ejemplo desde un proceso de campañas de marketing
        final var idDescuento = sut.concederDescuento(email, descripcion, valor, validoDesde, validoHasta);

        // Esperar la notificación
        final var eventoCaptor = ArgumentCaptor.forClass(DescuentoConcedidoEvent.class);
        verify(clienteEventConsumer).consume(eq(2L), eventoCaptor.capture());
        await().atMost(2, TimeUnit.SECONDS).until(() -> eventoCaptor.getValue() != null);

        // Invocado por el receptor de la notificación
        final var descuento = sut.getDescuento(email, idDescuento).orElseThrow();

        final var cliente = clienteRepository.load(email).orElseThrow();
        assertThat(cliente.getDescuentos())
                .extracting("id", "descripcion", "valor", "validoDesde", "validoHasta")
                .containsExactly(
                        tuple(idOtroDescuento, null, 0, null, null),
                        tuple(idDescuento, descripcion, valor, validoDesde, validoHasta));
        final var evento = eventoCaptor.getValue();
        assertThat(evento.getAggregateRootId()).isEqualTo(email);
        assertThat(evento.getId()).isEqualTo(idDescuento);
        assertThat(descuento.getId()).isEqualTo(idDescuento);
        assertThat(descuento.getDescripcion()).isEqualTo(descripcion);
        assertThat(descuento.getValor()).isEqualTo(valor);
        assertThat(descuento.getValidoDesde()).isEqualTo(validoDesde);
        assertThat(descuento.getValidoHasta()).isEqualTo(validoHasta);
    }
}
