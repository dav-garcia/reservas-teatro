package com.autentia.tutoriales.reservas.teatro.command;

import com.autentia.tutoriales.reservas.teatro.Butaca;
import com.autentia.tutoriales.reservas.teatro.Representacion;
import com.autentia.tutoriales.reservas.teatro.Reserva;
import com.autentia.tutoriales.reservas.teatro.error.CommandNotValidException;
import com.autentia.tutoriales.reservas.teatro.event.ReservaCreadaEvent;
import com.autentia.tutoriales.reservas.teatro.infra.event.EventPublisher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Transactional
public class RepresentacionCommandService {

    private final Repository<Representacion, UUID> representacionRepository;
    private final Repository<Reserva, UUID> reservaRepository;
    private final EventPublisher<UUID> reservaPublisher;

    public RepresentacionCommandService(final Repository<Representacion, UUID> representacionRepository,
                                        final Repository<Reserva, UUID> reservaRepository,
                                        final EventPublisher<UUID> reservaPublisher) {
        this.representacionRepository = representacionRepository;
        this.reservaRepository = reservaRepository;
        this.reservaPublisher = reservaPublisher;
    }

    public UUID seleccionarButacas(final UUID idRepresentacion, final Set<Butaca> butacas, final String email) {
        final var representacion = representacionRepository.load(idRepresentacion)
                .filter(r -> r.getButacasLibres().containsAll(butacas))
                .orElseThrow(() -> new CommandNotValidException("Representación no existe o las butacas no están libres"));
        final var idReserva = UUID.randomUUID();

        representacion.setVersion(representacion.getVersion() + 1);
        representacion.getButacasLibres().removeAll(butacas);

        representacionRepository.save(representacion);
        reservaRepository.save(Reserva.builder()
                .id(idReserva)
                .version(1L)
                .representacion(idRepresentacion)
                .butacas(butacas)
                .cliente(email)
                .build());
        // Double-write!
        reservaPublisher.tryPublish(0L, new ReservaCreadaEvent(idReserva, idRepresentacion, butacas, email));

        return idReserva;
    }
}
