package com.autentia.tutoriales.reservas.teatro;

import com.autentia.tutoriales.reservas.teatro.command.cliente.Cliente;
import com.autentia.tutoriales.reservas.teatro.command.cliente.ClienteCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.cliente.SuscribirClienteCommand;
import com.autentia.tutoriales.reservas.teatro.command.pago.ConfirmarPagoCommand;
import com.autentia.tutoriales.reservas.teatro.command.pago.Pago;
import com.autentia.tutoriales.reservas.teatro.command.pago.PagoCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.representacion.CrearRepresentacionCommand;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Representacion;
import com.autentia.tutoriales.reservas.teatro.command.representacion.RepresentacionCommandContext;
import com.autentia.tutoriales.reservas.teatro.command.representacion.SeleccionarButacasCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.CancelarReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ConfirmarReservaCommand;
import com.autentia.tutoriales.reservas.teatro.command.reserva.Reserva;
import com.autentia.tutoriales.reservas.teatro.command.reserva.ReservaCommandContext;
import com.autentia.tutoriales.reservas.teatro.configuration.ClienteConfiguration;
import com.autentia.tutoriales.reservas.teatro.configuration.HistoricoConfiguration;
import com.autentia.tutoriales.reservas.teatro.configuration.PagoConfiguration;
import com.autentia.tutoriales.reservas.teatro.configuration.RepresentacionConfiguration;
import com.autentia.tutoriales.reservas.teatro.configuration.ReservaConfiguration;
import com.autentia.tutoriales.reservas.teatro.configuration.SagaConfiguration;
import com.autentia.tutoriales.reservas.teatro.event.pago.Concepto;
import com.autentia.tutoriales.reservas.teatro.event.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.event.representacion.Sala;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.query.Historico;
import com.autentia.tutoriales.reservas.teatro.saga.ReservaSaga;
import org.apache.commons.lang3.RandomStringUtils;
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
        classes = {
                RepresentacionConfiguration.class, ReservaConfiguration.class, ClienteConfiguration.class, PagoConfiguration.class,
                SagaConfiguration.class, HistoricoConfiguration.class},
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
    private ReservaSaga reservaSaga; // Para el timeout de abandono

    @Autowired
    private Repository<Representacion, UUID> representacionRepository;

    @Autowired
    private Repository<Historico, String> historicoRepository;

    @Test
    public void givenSeleccionarButacasThenReservaCreada() {
        final var idRepresentacion = UUID.randomUUID();
        final var idReserva = UUID.randomUUID();
        final var idCliente = RandomStringUtils.randomAlphabetic(10) + "@email.com";

        representacionDispatcher.dispatch(new CrearRepresentacionCommand(idRepresentacion, ZonedDateTime.now(), SALA));
        representacionDispatcher.dispatch(new SeleccionarButacasCommand(idRepresentacion, idReserva, Set.of(A1, A2, B3), idCliente));

        await().atMost(2, TimeUnit.SECONDS).until(() -> historicoRepository.load(idCliente)
                .filter(r -> r.getReservas().containsKey(idReserva))
                .isPresent());

        final var representacion = representacionRepository.load(idRepresentacion).orElseThrow();
        final var historico = historicoRepository.load(idCliente).orElseThrow();
        final var reserva = historico.getReserva(idReserva);

        assertThat(representacion.getButacasLibres()).containsExactlyInAnyOrder(A3, B1, B2);
        assertThat(historico.isSuscrito()).isFalse();
        assertThat(historico.getNombre()).isNull();
        assertThat(historico.getDescuentos()).isEmpty();
        assertThat(reserva.getButacas()).containsExactlyInAnyOrder(A1, A2, B3);
        assertThat(reserva.getEstado()).isEqualTo(Historico.Reserva.Estado.CREADA);
    }

    @Test
    public void givenAbandonarReservaThenButacasLiberadas() {
        reservaSaga.setTimeout(1);
        try {
            final var idRepresentacion = UUID.randomUUID();
            final var idReserva = UUID.randomUUID();
            final var idCliente = RandomStringUtils.randomAlphabetic(10) + "@email.com";

            representacionDispatcher.dispatch(new CrearRepresentacionCommand(idRepresentacion, ZonedDateTime.now(), SALA));
            representacionDispatcher.dispatch(new SeleccionarButacasCommand(idRepresentacion, idReserva, Set.of(A1, A2, B3), idCliente));

            await().atMost(2, TimeUnit.SECONDS).until(() -> representacionRepository.load(idRepresentacion)
                    .filter(r -> r.getButacasLibres().containsAll(Set.of(A1, A2, B3)))
                    .isPresent());

            final var historico = historicoRepository.load(idCliente).orElseThrow();
            final var reserva = historico.getReserva(idReserva);

            assertThat(reserva.getEstado()).isEqualTo(Historico.Reserva.Estado.ABANDONADA);
        } finally {
            reservaSaga.setTimeout(null);
        }
    }

    @Test
    public void givenConfirmarReservaConDescuentoThenPagoPropuestoYDescuentoAplicado() {
        final var idRepresentacion = UUID.randomUUID();
        final var idReserva = UUID.randomUUID();
        final var idCliente = RandomStringUtils.randomAlphabetic(10) + "@email.com";

        representacionDispatcher.dispatch(new CrearRepresentacionCommand(idRepresentacion, ZonedDateTime.now(), SALA));
        representacionDispatcher.dispatch(new SeleccionarButacasCommand(idRepresentacion, idReserva, Set.of(A1, A2, B3), idCliente));
        clienteDispatcher.dispatch(new SuscribirClienteCommand(idCliente, "Cliente suscrito"));
        reservaDispatcher.dispatch(new ConfirmarReservaCommand(idReserva));

        await().atMost(2, TimeUnit.SECONDS).until(() -> historicoRepository.load(idCliente)
                .filter(h -> h.getReserva(idReserva).getPago() != null)
                .isPresent());

        final var historico = historicoRepository.load(idCliente).orElseThrow();
        final var reserva = historico.getReserva(idReserva);

        assertThat(historico.isSuscrito()).isTrue();
        assertThat(historico.getNombre()).isEqualTo("Cliente suscrito");
        assertThat(historico.getDescuentos()).isEmpty();
        assertThat(reserva.getDescuentos())
                .extracting("valor", "validoDesde", "validoHasta")
                .containsExactly(tuple(10, LocalDate.now(), LocalDate.now().plusDays(30)));
        assertThat(reserva.getConceptos()).containsExactlyInAnyOrder(
                new Concepto("Butaca A1", 10),
                new Concepto("Butaca A2", 20),
                new Concepto("Butaca B3", 30),
                new Concepto("Descuento por fidelización", -10));
        assertThat(reserva.getEstado()).isEqualTo(Historico.Reserva.Estado.CONFIRMADA);
    }

    @Test
    public void givenConfirmarPagoThenReservaPagada() {
        final var idRepresentacion = UUID.randomUUID();
        final var idReserva = UUID.randomUUID();
        final var idCliente = RandomStringUtils.randomAlphabetic(10) + "@email.com";

        representacionDispatcher.dispatch(new CrearRepresentacionCommand(idRepresentacion, ZonedDateTime.now(), SALA));
        representacionDispatcher.dispatch(new SeleccionarButacasCommand(idRepresentacion, idReserva, Set.of(A1, A2, B3), idCliente));
        reservaDispatcher.dispatch(new ConfirmarReservaCommand(idReserva));

        await().atMost(2, TimeUnit.SECONDS).until(() -> historicoRepository.load(idCliente)
                .filter(h -> h.getReserva(idReserva).getPago() != null)
                .isPresent());

        final var historico = historicoRepository.load(idCliente).orElseThrow();
        final var reserva = historico.getReserva(idReserva);

        pagoDispatcher.dispatch(new ConfirmarPagoCommand(reserva.getPago()));

        await().atMost(2, TimeUnit.SECONDS).until(() -> historicoRepository.load(idCliente)
                .filter(h -> h.getReserva(idReserva).getEstado() == Historico.Reserva.Estado.PAGADA)
                .isPresent());
    }

    @Test
    public void givenCancelarReservaConDescuentoWhenReservaConfirmadaThenButacasLiberadasYDescuentoRecuperado() {
        final var idRepresentacion = UUID.randomUUID();
        final var idReserva = UUID.randomUUID();
        final var idCliente = RandomStringUtils.randomAlphabetic(10) + "@email.com";

        representacionDispatcher.dispatch(new CrearRepresentacionCommand(idRepresentacion, ZonedDateTime.now(), SALA));
        representacionDispatcher.dispatch(new SeleccionarButacasCommand(idRepresentacion, idReserva, Set.of(A1, A2, B3), idCliente));
        clienteDispatcher.dispatch(new SuscribirClienteCommand(idCliente, "Cliente suscrito"));
        reservaDispatcher.dispatch(new ConfirmarReservaCommand(idReserva));

        await().atMost(2, TimeUnit.SECONDS).until(() -> historicoRepository.load(idCliente)
                .filter(h -> h.getReserva(idReserva).getPago() != null)
                .isPresent());

        reservaDispatcher.dispatch(new CancelarReservaCommand(idReserva));

        await().atMost(2, TimeUnit.SECONDS).until(() -> representacionRepository.load(idRepresentacion)
                .filter(r -> r.getButacasLibres().containsAll(Set.of(A1, A2, B3)))
                .isPresent());

        final var historico = historicoRepository.load(idCliente).orElseThrow();
        final var reserva = historico.getReserva(idReserva);

        assertThat(historico.getDescuentos())
                .extracting("valor", "validoDesde", "validoHasta")
                .containsExactly(tuple(10, LocalDate.now(), LocalDate.now().plusDays(30)));
        assertThat(reserva.getDescuentos()).isEmpty();
        assertThat(reserva.getEstado()).isEqualTo(Historico.Reserva.Estado.CANCELADA);
    }
}
