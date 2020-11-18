package com.autentia.tutoriales.reservas.teatro.saga;

import com.autentia.tutoriales.reservas.teatro.command.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.command.representacion.CrearRepresentacionCommand;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Representacion;
import com.autentia.tutoriales.reservas.teatro.command.representacion.RepresentacionEventConsumer;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Sala;
import com.autentia.tutoriales.reservas.teatro.command.representacion.SeleccionarButacasCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaEventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.occ.OccCommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepository;
import com.autentia.tutoriales.reservas.teatro.infra.event.inmemory.InMemoryEventPublisher;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservaTeatroSagaTest {

    private static final Butaca A1 = new Butaca("A", 1);
    private static final Butaca A3 = new Butaca("A", 3);
    private static final Butaca A5 = new Butaca("A", 5);
    private static final Butaca B1 = new Butaca("B", 1);
    private static final Butaca B3 = new Butaca("B", 3);
    private static final Butaca B5 = new Butaca("B", 5);
    private static final Sala SALA = new Sala("SALA", Set.of(A1, A3, A5, B1, B3, B5));

    private static final InMemoryEventPublisher<UUID> PUBLISHER = new InMemoryEventPublisher<>();
    private static final Repository<Representacion, UUID> REPRESENTACION_REPOSITORY = new InMemoryRepository<>();
    private static final Repository<Reserva, UUID> RESERVA_REPOSITORY = new InMemoryRepository<>();
    private static final CommandDispatcher<Representacion, UUID> REPRESENTACION_DISPATCHER = new OccCommandDispatcher<>(REPRESENTACION_REPOSITORY, PUBLISHER);
    private static final CommandDispatcher<Reserva, UUID> RESERVA_DISPATCHER = new OccCommandDispatcher<>(RESERVA_REPOSITORY, PUBLISHER);
    private static final RepresentacionEventConsumer REPRESENTACION_CONSUMER = new RepresentacionEventConsumer(REPRESENTACION_REPOSITORY);
    private static final ReservaEventConsumer RESERVA_CONSUMER = new ReservaEventConsumer(RESERVA_REPOSITORY);

    private static final ReservaTeatroSaga SUT = new ReservaTeatroSaga(RESERVA_DISPATCHER);

    @BeforeClass
    public static void setup() {
        PUBLISHER.registerEventConsumer(REPRESENTACION_CONSUMER);
        PUBLISHER.registerEventConsumer(RESERVA_CONSUMER);
        PUBLISHER.registerEventConsumer(SUT);
    }

    @Test
    public void givenSeleccionarButacasThenReservaCreada() {
        final var id = UUID.randomUUID();

        REPRESENTACION_DISPATCHER.dispatch(id, new CrearRepresentacionCommand(ZonedDateTime.now(), SALA));
        REPRESENTACION_DISPATCHER.dispatch(id, new SeleccionarButacasCommand(Set.of(A1, A3, B5)));

        final var representacion = REPRESENTACION_REPOSITORY.load(id).orElseThrow();
        final var reserva = RESERVA_REPOSITORY.find(r -> r.getRepresentacion().equals(id)).get(0);

        assertThat(representacion.getVersion()).isEqualTo(2L);
        assertThat(representacion.getButacasLibres()).containsExactlyInAnyOrder(A5, B1, B3);
        assertThat(reserva.getButacas()).containsExactlyInAnyOrder(A1, A3, B5);
    }
}
