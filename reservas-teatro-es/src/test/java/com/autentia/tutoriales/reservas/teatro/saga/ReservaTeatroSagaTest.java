package com.autentia.tutoriales.reservas.teatro.saga;

import com.autentia.tutoriales.reservas.teatro.command.cliente.Cliente;
import com.autentia.tutoriales.reservas.teatro.command.cliente.ClienteEventConsumer;
import com.autentia.tutoriales.reservas.teatro.command.cliente.SuscribirClienteCommand;
import com.autentia.tutoriales.reservas.teatro.command.pago.Concepto;
import com.autentia.tutoriales.reservas.teatro.command.pago.Pago;
import com.autentia.tutoriales.reservas.teatro.command.pago.PagoEventConsumer;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.command.representacion.CrearRepresentacionCommand;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Representacion;
import com.autentia.tutoriales.reservas.teatro.command.representacion.RepresentacionEventConsumer;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Sala;
import com.autentia.tutoriales.reservas.teatro.command.representacion.SeleccionarButacasCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ConfirmarReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaEventConsumer;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.occ.OccCommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.event.inmemory.InMemoryEventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepository;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;

public class ReservaTeatroSagaTest {

    private static final Butaca A1 = new Butaca("A", 1, 10);
    private static final Butaca A2 = new Butaca("A", 2, 20);
    private static final Butaca A3 = new Butaca("A", 3, 30);
    private static final Butaca B1 = new Butaca("B", 1, 10);
    private static final Butaca B2 = new Butaca("B", 2, 20);
    private static final Butaca B3 = new Butaca("B", 3, 30);
    private static final Sala SALA = new Sala("SALA", Set.of(A1, A2, A3, B1, B2, B3));

    private static final InMemoryEventPublisher<UUID> REPRESENTACION_PUBLISHER = new InMemoryEventPublisher<>();
    private static final InMemoryEventPublisher<UUID> RESERVA_PUBLISHER = new InMemoryEventPublisher<>();
    private static final InMemoryEventPublisher<String> CLIENTE_PUBLISHER = new InMemoryEventPublisher<>();
    private static final InMemoryEventPublisher<UUID> PAGO_PUBLISHER = new InMemoryEventPublisher<>();

    private static final Repository<Representacion, UUID> REPRESENTACION_REPOSITORY = new InMemoryRepository<>();
    private static final Repository<Reserva, UUID> RESERVA_REPOSITORY = new InMemoryRepository<>();
    private static final Repository<Cliente, String> CLIENTE_REPOSITORY = new InMemoryRepository<>();
    private static final Repository<Pago, UUID> PAGO_REPOSITORY = new InMemoryRepository<>();

    private static final CommandDispatcher<Representacion, UUID> REPRESENTACION_DISPATCHER = new OccCommandDispatcher<>(REPRESENTACION_REPOSITORY, REPRESENTACION_PUBLISHER);
    private static final CommandDispatcher<Reserva, UUID> RESERVA_DISPATCHER = new OccCommandDispatcher<>(RESERVA_REPOSITORY, RESERVA_PUBLISHER);
    private static final CommandDispatcher<Cliente, String> CLIENTE_DISPATCHER = new OccCommandDispatcher<>(CLIENTE_REPOSITORY, CLIENTE_PUBLISHER);
    private static final CommandDispatcher<Pago, UUID> PAGO_DISPATCHER = new OccCommandDispatcher<>(PAGO_REPOSITORY, PAGO_PUBLISHER);

    private static final ReservaTeatroSaga SUT = new ReservaTeatroSaga(
            RESERVA_REPOSITORY, CLIENTE_REPOSITORY,
            RESERVA_DISPATCHER, CLIENTE_DISPATCHER, PAGO_DISPATCHER);

    @BeforeClass
    public static void setup() {
        REPRESENTACION_PUBLISHER.registerEventConsumer(new RepresentacionEventConsumer(REPRESENTACION_REPOSITORY));
        RESERVA_PUBLISHER.registerEventConsumer(new ReservaEventConsumer(RESERVA_REPOSITORY));
        CLIENTE_PUBLISHER.registerEventConsumer(new ClienteEventConsumer(CLIENTE_REPOSITORY));
        PAGO_PUBLISHER.registerEventConsumer(new PagoEventConsumer(PAGO_REPOSITORY));

        REPRESENTACION_PUBLISHER.registerEventConsumer(SUT.getRepresentacionEventConsumer());
        RESERVA_PUBLISHER.registerEventConsumer(SUT.getReservaEventConsumer());
        CLIENTE_PUBLISHER.registerEventConsumer(SUT.getClienteEventConsumer());
    }

    @Test
    public void givenSeleccionarButacasThenReservaCreada() {
        final var idRepresentacion = UUID.randomUUID();
        final var idReserva = UUID.randomUUID();
        final var email = idReserva.toString() + "@test.com";

        REPRESENTACION_DISPATCHER.dispatch(new CrearRepresentacionCommand(idRepresentacion, ZonedDateTime.now(), SALA));
        REPRESENTACION_DISPATCHER.dispatch(new SeleccionarButacasCommand(idRepresentacion, idReserva, Set.of(A1, A2, B3), email));

        final var representacion = REPRESENTACION_REPOSITORY.load(idRepresentacion).orElseThrow();
        final var cliente = CLIENTE_REPOSITORY.load(email).orElseThrow();
        final var reserva = RESERVA_REPOSITORY.load(idReserva).orElseThrow();

        assertThat(representacion.getVersion()).isEqualTo(2L);
        assertThat(representacion.getButacasLibres()).containsExactlyInAnyOrder(A3, B1, B2);
        assertThat(cliente.getVersion()).isEqualTo(1L);
        assertThat(cliente.isSuscrito()).isFalse();
        assertThat(cliente.getNombre()).isNull();
        assertThat(cliente.getDescuentos()).isEmpty();
        assertThat(reserva.getVersion()).isEqualTo(1L);
        assertThat(reserva.getButacas()).containsExactlyInAnyOrder(A1, A2, B3);
        assertThat(reserva.getCliente()).isEqualTo(email);
    }

    @Test
    public void givenSeleccionarButacasWhenTimeoutThenReservaCancelada() {
        SUT.setTimeout(1);
        try {
            final var idRepresentacion = UUID.randomUUID();
            final var idReserva = UUID.randomUUID();
            final var email = idReserva.toString() + "@test.com";

            REPRESENTACION_DISPATCHER.dispatch(new CrearRepresentacionCommand(idRepresentacion, ZonedDateTime.now(), SALA));
            REPRESENTACION_DISPATCHER.dispatch(new SeleccionarButacasCommand(idRepresentacion, idReserva, Set.of(A1, A2, B3), email));

            await().atMost(2, TimeUnit.SECONDS).until(() -> RESERVA_REPOSITORY.load(idReserva).isEmpty());
            // TODO: Assert butacas devueltas
        } finally {
            SUT.setTimeout(null);
        }
    }

    @Test
    public void givenConfirmarReservaThenPagoPropuesto() {
        final var idRepresentacion = UUID.randomUUID();
        final var idReserva = UUID.randomUUID();
        final var email = idReserva.toString() + "@test.com";

        REPRESENTACION_DISPATCHER.dispatch(new CrearRepresentacionCommand(idRepresentacion, ZonedDateTime.now(), SALA));
        REPRESENTACION_DISPATCHER.dispatch(new SeleccionarButacasCommand(idRepresentacion, idReserva, Set.of(A1, A2, B3), email));
        CLIENTE_DISPATCHER.dispatch(new SuscribirClienteCommand(email, "Cliente suscrito"));
        RESERVA_DISPATCHER.dispatch(new ConfirmarReservaCommand(idReserva));

        final var cliente = CLIENTE_REPOSITORY.load(email).orElseThrow();
        final var reserva = RESERVA_REPOSITORY.load(idReserva).orElseThrow();
        final var pago = PAGO_REPOSITORY.find(p -> p.getReserva().equals(idReserva)).get(0);

        assertThat(cliente.getVersion()).isEqualTo(4L);
        assertThat(cliente.isSuscrito()).isTrue();
        assertThat(cliente.getNombre()).isEqualTo("Cliente suscrito");
        assertThat(cliente.getDescuentos())
                .extracting("valor", "validoDesde", "validoHasta", "enReserva")
                .containsExactly(tuple(10, LocalDate.now(), LocalDate.now().plusDays(30), idReserva));
        assertThat(reserva.getVersion()).isEqualTo(2L);
        assertThat(reserva.getButacas()).containsExactlyInAnyOrder(A1, A2, B3);
        assertThat(reserva.getCliente()).isEqualTo(email);
        assertThat(reserva.isConfirmada()).isTrue();
        assertThat(pago.getVersion()).isEqualTo(1L);
        assertThat(pago.getReserva()).isEqualTo(idReserva);
        assertThat(pago.getCliente()).isEqualTo(email);
        assertThat(pago.getConceptos()).containsExactlyInAnyOrder(
                new Concepto("Butaca A1", 10),
                new Concepto("Butaca A2", 20),
                new Concepto("Butaca B3", 30),
                new Concepto("Descuento por fidelización", -10));
    }
}
