package com.autentia.tutoriales.reservas.teatro.infra.dispatcher.syncrhonized;

import com.autentia.tutoriales.reservas.teatro.command.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.command.representacion.CrearRepresentacionCommand;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Representacion;
import com.autentia.tutoriales.reservas.teatro.command.representacion.RepresentacionEventHandler;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Sala;
import com.autentia.tutoriales.reservas.teatro.command.representacion.SeleccionarButacasCommand;
import com.autentia.tutoriales.reservas.teatro.infra.dispatcher.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.handler.inmemory.InMemorySyncEventStreamFactory;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepository;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SynchronizedCommandDispatcherTest {

    private static final Butaca A1 = new Butaca("A", 1);
    private static final Butaca A3 = new Butaca("A", 3);
    private static final Butaca A5 = new Butaca("A", 5);
    private static final Butaca B1 = new Butaca("B", 1);
    private static final Sala SALA = new Sala("SALA", Set.of(A1, A3, A5, B1));

    private static final Repository<Representacion, UUID> REPOSITORY = new InMemoryRepository<>();
    private static final InMemorySyncEventStreamFactory FACTORY = new InMemorySyncEventStreamFactory();

    private static final CommandDispatcher SUT = new SynchronizedCommandDispatcher(FACTORY);

    @BeforeClass
    public static void setup() {
        FACTORY.putEventHandler(Representacion.class, new RepresentacionEventHandler(REPOSITORY));
    }

    @Test
    public void givenComandosValidosThenProyectaEventos() {
        final var id = UUID.randomUUID();
        final var command1 = new CrearRepresentacionCommand(ZonedDateTime.now(), SALA);
        final var command2 = new SeleccionarButacasCommand(Set.of(A1, A3));

        SUT.dispatch(command1, Representacion.builder().id(id).build());
        SUT.dispatch(command2, REPOSITORY.load(id).orElseThrow());

        final var result = REPOSITORY.load(id).orElseThrow();
        assertThat(result.getButacasLibres()).containsExactlyInAnyOrder(A5, B1);
    }
}
