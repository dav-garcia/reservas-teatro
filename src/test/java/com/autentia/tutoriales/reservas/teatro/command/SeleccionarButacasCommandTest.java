package com.autentia.tutoriales.reservas.teatro.command;

import com.autentia.tutoriales.reservas.teatro.command.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Representacion;
import com.autentia.tutoriales.reservas.teatro.command.representacion.RepresentacionEventHandler;
import com.autentia.tutoriales.reservas.teatro.command.representacion.SeleccionarButacasCommand;
import com.autentia.tutoriales.reservas.teatro.dispatcher.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.dispatcher.syncrhonized.SynchronizedCommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.handler.inmemory.InMemorySyncEventStreamFactory;
import com.autentia.tutoriales.reservas.teatro.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.repository.inmemory.InMemoryRepository;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SeleccionarButacasCommandTest {

    private static final Butaca A1 = new Butaca("A", 1);
    private static final Butaca A3 = new Butaca("A", 3);
    private static final Butaca A5 = new Butaca("A", 5);
    private static final Butaca B1 = new Butaca("B", 1);

    private static final Repository<Representacion, UUID> REPOSITORY = new InMemoryRepository<>();
    private static final InMemorySyncEventStreamFactory FACTORY = new InMemorySyncEventStreamFactory();
    private static final CommandDispatcher DISPATCHER = new SynchronizedCommandDispatcher(FACTORY);

    @BeforeClass
    public static void setup() {
        FACTORY.putEventHandler(Representacion.class, new RepresentacionEventHandler(REPOSITORY));
    }

    @Test
    public void givenValidCommandThenPublishEvents() {
        final var butacasLibres = new HashSet<>(List.of(A1, A3, A5, B1)); // Modificable
        final var butacasReserva = Set.of(A1, A3);
        final var root = Representacion.builder()
                .id(UUID.randomUUID())
                .butacasLibres(butacasLibres)
                .build();
        final var command = new SeleccionarButacasCommand(butacasReserva);

        REPOSITORY.create(root);
        DISPATCHER.dispatch(command, root);

        assertThat(root.getButacasLibres()).containsExactlyInAnyOrder(A5, B1);
    }
}
