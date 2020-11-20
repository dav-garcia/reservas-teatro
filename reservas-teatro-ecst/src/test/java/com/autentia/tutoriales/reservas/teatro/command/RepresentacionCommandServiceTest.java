package com.autentia.tutoriales.reservas.teatro.command;

import com.autentia.tutoriales.reservas.teatro.Butaca;
import com.autentia.tutoriales.reservas.teatro.Representacion;
import com.autentia.tutoriales.reservas.teatro.Reserva;
import com.autentia.tutoriales.reservas.teatro.Sala;
import com.autentia.tutoriales.reservas.teatro.event.ReservaCreadaEvent;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.event.inmemory.InMemoryEventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RepresentacionCommandServiceTest {

    private static final Butaca A1 = new Butaca("A", 1);
    private static final Butaca A2 = new Butaca("A", 2);
    private static final Butaca A3 = new Butaca("A", 3);
    private static final Butaca B1 = new Butaca("B", 1);
    private static final Butaca B2 = new Butaca("B", 2);
    private static final Butaca B3 = new Butaca("B", 3);
    private static final Sala SALA = new Sala("SALA", Set.of(A1, A2, A3, B1, B2, B3));
    private static final String EMAIL = "cliente@email.com";

    private static final Repository<Representacion, UUID> REPRESENTACION_REPOSITORY = new InMemoryRepository<>();
    private static final Repository<Reserva, UUID> RESERVA_REPOSITORY = new InMemoryRepository<>();
    private static final InMemoryEventPublisher<UUID> RESERVA_EVENT_PUBLISHER = new InMemoryEventPublisher<>();

    private static final RepresentacionCommandService SUT = new RepresentacionCommandService(
            REPRESENTACION_REPOSITORY, RESERVA_REPOSITORY, RESERVA_EVENT_PUBLISHER);

    @Mock
    private EventConsumer<UUID> reservaEventConsumer;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void givenSeleccionarButacasThenReservaCreada() {
        final var idRepresentacion = UUID.randomUUID();
        REPRESENTACION_REPOSITORY.save(Representacion.builder()
                .id(idRepresentacion)
                .version(1L)
                .cuando(ZonedDateTime.now())
                .donde(SALA)
                .butacasLibres(new HashSet<>(SALA.getButacas()))
                .build());
        RESERVA_EVENT_PUBLISHER.registerEventConsumer(reservaEventConsumer);

        final var idReserva = SUT.seleccionarButacas(idRepresentacion, Set.of(A1, A2, B3), EMAIL);

        final var representacion = REPRESENTACION_REPOSITORY.load(idRepresentacion).orElseThrow();
        assertThat(representacion.getVersion()).isEqualTo(2L);
        assertThat(representacion.getButacasLibres()).containsExactlyInAnyOrder(A3, B1, B2);
        final var reserva = RESERVA_REPOSITORY.load(idReserva).orElseThrow();
        assertThat(reserva.getVersion()).isEqualTo(1L);
        assertThat(reserva.getRepresentacion()).isEqualTo(idRepresentacion);
        assertThat(reserva.getButacas()).containsExactlyInAnyOrder(A1, A2, B3);
        assertThat(reserva.getCliente()).isEqualTo(EMAIL);
        final var eventoCaptor = ArgumentCaptor.forClass(ReservaCreadaEvent.class);
        verify(reservaEventConsumer).consume(eq(1L), eventoCaptor.capture());
        final var evento = eventoCaptor.getValue();
        assertThat(evento.getAggregateRootId()).isEqualTo(idReserva);
        assertThat(evento.getRepresentacion()).isEqualTo(idRepresentacion);
        assertThat(evento.getButacas()).containsExactlyInAnyOrder(A1, A2, B3);
        assertThat(evento.getCliente()).isEqualTo(EMAIL);
    }
}
