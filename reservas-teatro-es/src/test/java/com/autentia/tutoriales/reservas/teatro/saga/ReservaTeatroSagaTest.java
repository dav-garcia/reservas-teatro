package com.autentia.tutoriales.reservas.teatro.saga;

import com.autentia.tutoriales.reservas.teatro.command.cliente.Cliente;
import com.autentia.tutoriales.reservas.teatro.command.cliente.ClienteEventConsumer;
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
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class ReservaTeatroSagaTest {

    private static final Butaca A1 = new Butaca("A", 1);
    private static final Butaca A2 = new Butaca("A", 2);
    private static final Butaca A3 = new Butaca("A", 3);
    private static final Butaca B1 = new Butaca("B", 1);
    private static final Butaca B2 = new Butaca("B", 2);
    private static final Butaca B3 = new Butaca("B", 3);
    private static final Sala SALA = new Sala("SALA", Set.of(A1, A2, A3, B1, B2, B3));
    private static final String EMAIL = "cliente@email.com";

    private static final InMemoryEventPublisher<UUID> REPRESENTACION_PUBLISHER = new InMemoryEventPublisher<>();
    private static final InMemoryEventPublisher<UUID> RESERVA_PUBLISHER = new InMemoryEventPublisher<>();
    private static final InMemoryEventPublisher<String> CLIENTE_PUBLISHER = new InMemoryEventPublisher<>();

    private static final Repository<Representacion, UUID> REPRESENTACION_REPOSITORY = new InMemoryRepository<>();
    private static final Repository<Reserva, UUID> RESERVA_REPOSITORY = new InMemoryRepository<>();
    private static final Repository<Cliente, String> CLIENTE_REPOSITORY = new InMemoryRepository<>();

    private static final CommandDispatcher<Representacion, UUID> REPRESENTACION_DISPATCHER = new OccCommandDispatcher<>(REPRESENTACION_REPOSITORY, REPRESENTACION_PUBLISHER);
    private static final CommandDispatcher<Reserva, UUID> RESERVA_DISPATCHER = new OccCommandDispatcher<>(RESERVA_REPOSITORY, RESERVA_PUBLISHER);
    private static final CommandDispatcher<Cliente, String> CLIENTE_DISPATCHER = new OccCommandDispatcher<>(CLIENTE_REPOSITORY, CLIENTE_PUBLISHER);

    private static final RepresentacionEventConsumer REPRESENTACION_CONSUMER = new RepresentacionEventConsumer(REPRESENTACION_REPOSITORY);
    private static final ReservaEventConsumer RESERVA_CONSUMER = new ReservaEventConsumer(RESERVA_REPOSITORY);
    private static final ClienteEventConsumer CLIENTE_CONSUMER = new ClienteEventConsumer(CLIENTE_REPOSITORY);

    private static final ReservaTeatroSaga SUT = new ReservaTeatroSaga(RESERVA_DISPATCHER, CLIENTE_DISPATCHER);

    @BeforeClass
    public static void setup() {
        REPRESENTACION_PUBLISHER.registerEventConsumer(REPRESENTACION_CONSUMER);
        RESERVA_PUBLISHER.registerEventConsumer(RESERVA_CONSUMER);
        CLIENTE_PUBLISHER.registerEventConsumer(CLIENTE_CONSUMER);

        REPRESENTACION_PUBLISHER.registerEventConsumer(SUT.getRepresentacionEventConsumer());
        RESERVA_PUBLISHER.registerEventConsumer(SUT.getReservaEventConsumer());
        CLIENTE_PUBLISHER.registerEventConsumer(SUT.getClienteEventConsumer());
    }

    @Test
    public void givenSeleccionarButacasThenReservaCreada() {
        final var idRepresentacion = UUID.randomUUID();

        REPRESENTACION_DISPATCHER.dispatch(new CrearRepresentacionCommand(idRepresentacion, ZonedDateTime.now(), SALA));
        REPRESENTACION_DISPATCHER.dispatch(new SeleccionarButacasCommand(idRepresentacion, Set.of(A1, A2, B3), EMAIL));

        final var representacion = REPRESENTACION_REPOSITORY.load(idRepresentacion).orElseThrow();
        final var cliente = CLIENTE_REPOSITORY.load(EMAIL).orElseThrow();
        final var reserva = RESERVA_REPOSITORY.find(r -> r.getRepresentacion().equals(idRepresentacion)).get(0);

        assertThat(representacion.getVersion()).isEqualTo(2L);
        assertThat(representacion.getButacasLibres()).containsExactlyInAnyOrder(A3, B1, B2);
        assertThat(cliente.getVersion()).isEqualTo(1L);
        assertThat(cliente.isSuscrito()).isFalse();
        assertThat(cliente.getNombre()).isNull();
        assertThat(cliente.getDescuentos()).isEmpty();
        assertThat(reserva.getVersion()).isEqualTo(1L);
        assertThat(reserva.getButacas()).containsExactlyInAnyOrder(A1, A2, B3);
        assertThat(reserva.getCliente()).isEqualTo(EMAIL);
    }

    @Test
    public void givenSeleccionarButacasWhenTimeoutThenReservaCancelada() {
        SUT.setTimeout(1);
        try {
            final var idRepresentacion = UUID.randomUUID();

            REPRESENTACION_DISPATCHER.dispatch(new CrearRepresentacionCommand(idRepresentacion, ZonedDateTime.now(), SALA));
            REPRESENTACION_DISPATCHER.dispatch(new SeleccionarButacasCommand(idRepresentacion, Set.of(A1, A2, B3), EMAIL));

            await().atMost(2, TimeUnit.SECONDS).until(() ->
                    RESERVA_REPOSITORY.find(r -> r.getRepresentacion().equals(idRepresentacion)).isEmpty());
            // TODO: Assert butacas devueltas
        } finally {
            SUT.setTimeout(null);
        }
    }
}
