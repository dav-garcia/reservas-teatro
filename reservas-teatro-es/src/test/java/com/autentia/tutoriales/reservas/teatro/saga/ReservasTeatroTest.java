package com.autentia.tutoriales.reservas.teatro.saga;

import com.autentia.tutoriales.reservas.teatro.command.cliente.Cliente;
import com.autentia.tutoriales.reservas.teatro.command.cliente.ClienteCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.cliente.SuscribirClienteCommand;
import com.autentia.tutoriales.reservas.teatro.command.pago.Pago;
import com.autentia.tutoriales.reservas.teatro.command.pago.PagoCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.command.representacion.CrearRepresentacionCommand;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Representacion;
import com.autentia.tutoriales.reservas.teatro.command.representacion.RepresentacionCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Sala;
import com.autentia.tutoriales.reservas.teatro.command.representacion.SeleccionarButacasCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ConfirmarReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaCommandContext;
import com.autentia.tutoriales.reservas.teatro.configuration.ClienteConfiguration;
import com.autentia.tutoriales.reservas.teatro.configuration.PagoConfiguration;
import com.autentia.tutoriales.reservas.teatro.configuration.RepresentacionConfiguration;
import com.autentia.tutoriales.reservas.teatro.configuration.ReservaConfiguration;
import com.autentia.tutoriales.reservas.teatro.configuration.SagaConfiguration;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {RepresentacionConfiguration.class, ReservaConfiguration.class, ClienteConfiguration.class, PagoConfiguration.class, SagaConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ReservasTeatroTest {

    private static final Butaca A1 = new Butaca("A", 1, 10);
    private static final Butaca A2 = new Butaca("A", 2, 20);
    private static final Butaca A3 = new Butaca("A", 3, 30);
    private static final Butaca B1 = new Butaca("B", 1, 10);
    private static final Butaca B2 = new Butaca("B", 2, 20);
    private static final Butaca B3 = new Butaca("B", 3, 30);
    private static final Sala SALA = new Sala("SALA", Set.of(A1, A2, A3, B1, B2, B3));

    @Autowired
    private CommandDispatcher<RepresentacionCommandContext, Representacion, UUID> representacionDispatcher;

    @Autowired
    private CommandDispatcher<ReservaCommandContext, Reserva, UUID> reservaDispatcher;

    @Autowired
    private CommandDispatcher<ClienteCommandContext, Cliente, String> clienteDispatcher;

    @Autowired
    private CommandDispatcher<PagoCommandContext, Pago, UUID> pagoDispatcher;

    @Autowired
    private ReservaSaga reservaSaga;

    @Autowired
    private Repository<Representacion, UUID> representacionRepository;

    @Autowired
    private Repository<Reserva, UUID> reservaRepository;

    @Autowired
    private Repository<Cliente, String> clienteRepository;

    @Autowired
    private Repository<Pago, UUID> pagoRepository;

    @Test
    public void givenButacasSeleccionadasThenReservaCreada() {
        final var idRepresentacion = UUID.randomUUID();
        final var idReserva = UUID.randomUUID();
        final var email = idReserva.toString() + "@test.com";

        representacionDispatcher.dispatch(new CrearRepresentacionCommand(idRepresentacion, ZonedDateTime.now(), SALA));
        representacionDispatcher.dispatch(new SeleccionarButacasCommand(idRepresentacion, idReserva, Set.of(A1, A2, B3), email));

        final var representacion = representacionRepository.load(idRepresentacion).orElseThrow();
        final var cliente = clienteRepository.load(email).orElseThrow();
        final var reserva = reservaRepository.load(idReserva).orElseThrow();

        assertThat(representacion.getVersion()).isEqualTo(2L);
        assertThat(representacion.getButacasLibres()).containsExactlyInAnyOrder(A3, B1, B2);
        assertThat(cliente.getVersion()).isEqualTo(1L);
        assertThat(cliente.isSuscrito()).isFalse();
        assertThat(cliente.getNombre()).isNull();
        assertThat(cliente.getDescuentos()).isEmpty();
        assertThat(reserva.getVersion()).isEqualTo(1L);
        assertThat(reserva.getButacas()).containsExactlyInAnyOrder(A1, A2, B3);
        assertThat(reserva.getCliente()).isEqualTo(email);
        assertThat(reserva.getEstado()).isEqualTo(Reserva.Estado.CREADA);
    }

    @Test
    public void givenButacasSeleccionadasWhenTimeoutThenReservaCancelada() {
        reservaSaga.setTimeout(1);
        try {
            final var idRepresentacion = UUID.randomUUID();
            final var idReserva = UUID.randomUUID();
            final var email = idReserva.toString() + "@test.com";

            representacionDispatcher.dispatch(new CrearRepresentacionCommand(idRepresentacion, ZonedDateTime.now(), SALA));
            representacionDispatcher.dispatch(new SeleccionarButacasCommand(idRepresentacion, idReserva, Set.of(A1, A2, B3), email));

            await().atMost(2, TimeUnit.SECONDS).until(() -> reservaRepository.load(idReserva)
                    .filter(r -> r.getEstado() == Reserva.Estado.ABANDONADA)
                    .isPresent());

            final var representacion = representacionRepository.load(idRepresentacion).orElseThrow();

            assertThat(representacion.getButacasLibres()).contains(A1, A2, A3, B1, B2, B3);
        } finally {
            reservaSaga.setTimeout(null);
        }
    }

    @Test
    public void givenReservaConfirmadaThenPagoPropuesto() {
        final var idRepresentacion = UUID.randomUUID();
        final var idReserva = UUID.randomUUID();
        final var email = idReserva.toString() + "@test.com";

        representacionDispatcher.dispatch(new CrearRepresentacionCommand(idRepresentacion, ZonedDateTime.now(), SALA));
        representacionDispatcher.dispatch(new SeleccionarButacasCommand(idRepresentacion, idReserva, Set.of(A1, A2, B3), email));
        clienteDispatcher.dispatch(new SuscribirClienteCommand(email, "Cliente suscrito"));
        reservaDispatcher.dispatch(new ConfirmarReservaCommand(idReserva));

        final var cliente = clienteRepository.load(email).orElseThrow();
        final var reserva = reservaRepository.load(idReserva).orElseThrow();
        //final var pago = pagoRepository.find(p -> p.getReserva().equals(idReserva)).get(0);

        assertThat(cliente.getVersion()).isEqualTo(4L);
        assertThat(cliente.isSuscrito()).isTrue();
        assertThat(cliente.getNombre()).isEqualTo("Cliente suscrito");
        assertThat(cliente.getDescuentos())
                .extracting("valor", "validoDesde", "validoHasta", "enReserva")
                .containsExactly(tuple(10, LocalDate.now(), LocalDate.now().plusDays(30), idReserva));
        assertThat(reserva.getVersion()).isEqualTo(2L);
        assertThat(reserva.getButacas()).containsExactlyInAnyOrder(A1, A2, B3);
        assertThat(reserva.getCliente()).isEqualTo(email);
        assertThat(reserva.getEstado()).isEqualTo(Reserva.Estado.CONFIRMADA);
        /*assertThat(pago.getVersion()).isEqualTo(1L);
        assertThat(pago.getReserva()).isEqualTo(idReserva);
        assertThat(pago.getCliente()).isEqualTo(email);
        assertThat(pago.getConceptos()).containsExactlyInAnyOrder(
                new Concepto("Butaca A1", 10),
                new Concepto("Butaca A2", 20),
                new Concepto("Butaca B3", 30),
                new Concepto("Descuento por fidelización", -10));
        assertThat(pago.getCodigoPago()).isNotNull();*/
    }
}
