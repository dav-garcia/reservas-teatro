package com.autentia.tutoriales.reservas.teatro.infra.dispatch.syncrhonized;

import com.autentia.tutoriales.reservas.teatro.command.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.command.representacion.CrearRepresentacionCommand;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Representacion;
import com.autentia.tutoriales.reservas.teatro.command.representacion.Sala;
import com.autentia.tutoriales.reservas.teatro.command.representacion.SeleccionarButacasCommand;
import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.infra.EventSourceId;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory.InMemoryRepository;
import com.autentia.tutoriales.reservas.teatro.infra.stream.inmemory.InMemorySyncEventStreamFactory;
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
    private static final Butaca B3 = new Butaca("B", 3);
    private static final Butaca B5 = new Butaca("B", 5);
    private static final Sala SALA = new Sala("SALA", Set.of(A1, A3, A5, B1, B3, B5));

    private static final Repository<Representacion, UUID> REPOSITORY = new InMemoryRepository<>();
    private static final InMemorySyncEventStreamFactory EVENT_STREAM_FACTORY = new InMemorySyncEventStreamFactory();

    private static final CommandDispatcher SUT = new SynchronizedCommandDispatcher(EVENT_STREAM_FACTORY);

    @BeforeClass
    public static void setup() {
        EVENT_STREAM_FACTORY.registerAggregateRoot(Representacion.class, REPOSITORY);
    }

    @Test
    public void givenValidCommandsThenProjectEvents() throws CommandNotValidException {
        final var id = EventSourceId.of(Representacion.class, UUID.randomUUID());

        SUT.registerAggregateRoot(Representacion.class, REPOSITORY);
        SUT.dispatch(new CrearRepresentacionCommand(ZonedDateTime.now(), SALA), id);
        SUT.dispatch(new SeleccionarButacasCommand(Set.of(A1, A3, B5)), id);

        final var result = REPOSITORY.load(id.getValue()).orElseThrow();
        assertThat(result.getVersion()).isEqualTo(2L);
        assertThat(result.getButacasLibres()).containsExactlyInAnyOrder(A5, B1, B3);
    }
}
