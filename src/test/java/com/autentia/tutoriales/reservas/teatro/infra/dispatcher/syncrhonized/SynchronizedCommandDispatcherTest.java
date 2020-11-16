package com.autentia.tutoriales.reservas.teatro.infra.dispatcher.syncrhonized;

import com.autentia.tutoriales.reservas.teatro.command.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.command.representacion.CrearRepresentacionCommand;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Representacion;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Sala;
import com.autentia.tutoriales.reservas.teatro.command.representacion.SeleccionarButacasCommand;
import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.EventSourceId;
import com.autentia.tutoriales.reservas.teatro.infra.dispatcher.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.dispatcher.randomasync.RandomAsyncCommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.handler.inmemory.InMemorySyncEventStreamFactory;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepository;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class SynchronizedCommandDispatcherTest {

    private static final Butaca A1 = new Butaca("A", 1);
    private static final Butaca A3 = new Butaca("A", 3);
    private static final Butaca A5 = new Butaca("A", 5);
    private static final Butaca B1 = new Butaca("B", 1);
    private static final Sala SALA = new Sala("SALA", Set.of(A1, A3, A5, B1));

    private static final CrearRepresentacionCommand CREAR_COMMAND =
            new CrearRepresentacionCommand(ZonedDateTime.now(), SALA);
    private static final SeleccionarButacasCommand SELECCIONAR_COMMAND =
            new SeleccionarButacasCommand(Set.of(A1, A3));

    private static final Repository<Representacion, UUID> REPOSITORY = new InMemoryRepository<>(i -> Representacion.builder().id(i).build());
    private static final InMemorySyncEventStreamFactory EVENT_STREAM_FACTORY = new InMemorySyncEventStreamFactory();

    private static final CommandDispatcher SUT = new SynchronizedCommandDispatcher(EVENT_STREAM_FACTORY);

    @BeforeClass
    public static void setup() {
        EVENT_STREAM_FACTORY.registerAggregateRoot(Representacion.class, REPOSITORY);
    }

    @Test
    public void givenValidCommandsThenProjectEvents() {
        final var id = UUID.randomUUID();

        SUT.registerAggregateRoot(Representacion.class, REPOSITORY);
        SUT.dispatch(CREAR_COMMAND, EventSourceId.of(Representacion.class, id));
        SUT.dispatch(SELECCIONAR_COMMAND, EventSourceId.of(Representacion.class, id));

        final var result = REPOSITORY.load(id).orElseThrow();
        assertThat(result.getVersion()).isEqualTo(2L);
        assertThat(result.getButacasLibres()).containsExactlyInAnyOrder(A5, B1);
    }

    @Test
    public void givenValidCommandsWhenRandomAsyncDispatcherThenFail() {
        final var randomAsyncDispatcher = new RandomAsyncCommandDispatcher(SUT, new SynchronizedCommandDispatcher(EVENT_STREAM_FACTORY));

        try {
            randomAsyncDispatcher.registerAggregateRoot(Representacion.class, REPOSITORY);
            while (true) {
                final var id = UUID.randomUUID();

                randomAsyncDispatcher.dispatch(CREAR_COMMAND, EventSourceId.of(Representacion.class, id));
                randomAsyncDispatcher.dispatch(SELECCIONAR_COMMAND, EventSourceId.of(Representacion.class, id));

                await().atMost(1, TimeUnit.SECONDS).pollInterval(10, TimeUnit.MILLISECONDS).until(() -> {
                    final var result = REPOSITORY.load(id);
                    return result.filter(r -> r.getVersion() == 2L && r.getButacasLibres().equals(Set.of(A5, B1))).isPresent();
                });
            }
        } catch (CommandNotValidException|ConditionTimeoutException exception) {
            // Fallo esperado
        }
    }
}
