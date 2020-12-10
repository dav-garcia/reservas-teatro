package com.autentia.tutoriales.reservas.teatro.command;

import com.autentia.tutoriales.reservas.teatro.Butaca;
import com.autentia.tutoriales.reservas.teatro.Representacion;
import com.autentia.tutoriales.reservas.teatro.Reserva;
import com.autentia.tutoriales.reservas.teatro.Sala;
import com.autentia.tutoriales.reservas.teatro.configuration.EcstConfiguration;
import com.autentia.tutoriales.reservas.teatro.event.ReservaCreadaEvent;
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

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = EcstConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class RepresentacionCommandServiceTest {

    private static final Butaca A1 = new Butaca("A", 1);
    private static final Butaca A2 = new Butaca("A", 2);
    private static final Butaca A3 = new Butaca("A", 3);
    private static final Butaca B1 = new Butaca("B", 1);
    private static final Butaca B2 = new Butaca("B", 2);
    private static final Butaca B3 = new Butaca("B", 3);
    private static final Sala SALA = new Sala("SALA", Set.of(A1, A2, A3, B1, B2, B3));

    @Autowired
    private Repository<Representacion, UUID> representacionRepository;

    @Autowired
    private Repository<Reserva, UUID> reservaRepository;

    @Autowired
    private InMemoryEventPublisher<UUID> reservaPublisher;

    @Autowired
    private RepresentacionCommandService sut;

    @MockBean
    private EventConsumer<UUID> reservaEventConsumer;

    @Test
    public void givenSeleccionarButacasThenReservaCreada() {
        final var idRepresentacion = UUID.randomUUID();
        final var idCliente = RandomStringUtils.randomAlphabetic(10) + "@email.com";

        representacionRepository.save(Representacion.builder()
                .id(idRepresentacion)
                .version(1L)
                .cuando(ZonedDateTime.now())
                .donde(SALA)
                .butacasLibres(new HashSet<>(SALA.getButacas()))
                .build());
        reservaPublisher.registerEventConsumer(reservaEventConsumer);

        final var idReserva = sut.seleccionarButacas(idRepresentacion, Set.of(A1, A2, B3), idCliente);

        final var eventoCaptor = ArgumentCaptor.forClass(ReservaCreadaEvent.class);
        verify(reservaEventConsumer).consume(eq(1L), eventoCaptor.capture());
        await().atMost(2, TimeUnit.SECONDS).until(() -> eventoCaptor.getValue() != null);

        final var representacion = representacionRepository.load(idRepresentacion).orElseThrow();
        assertThat(representacion.getVersion()).isEqualTo(2L);
        assertThat(representacion.getButacasLibres()).containsExactlyInAnyOrder(A3, B1, B2);
        final var reserva = reservaRepository.load(idReserva).orElseThrow();
        assertThat(reserva.getVersion()).isEqualTo(1L);
        assertThat(reserva.getRepresentacion()).isEqualTo(idRepresentacion);
        assertThat(reserva.getButacas()).containsExactlyInAnyOrder(A1, A2, B3);
        assertThat(reserva.getCliente()).isEqualTo(idCliente);
        final var evento = eventoCaptor.getValue();
        assertThat(evento.getAggregateRootId()).isEqualTo(idReserva);
        assertThat(evento.getRepresentacion()).isEqualTo(idRepresentacion);
        assertThat(evento.getButacas()).containsExactlyInAnyOrder(A1, A2, B3);
        assertThat(evento.getCliente()).isEqualTo(idCliente);
    }
}
