package com.autentia.tutoriales.reservas.teatro.command.reserva;

import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import org.springframework.lang.NonNull;

import java.util.UUID;

/**
 * Para que los Command sean auténticos POJO, necesitan un apoyo para
 * poder acceder a los servicios de infraestructura
 */
public class ReservaCommandSupport {

    private static Repository<Reserva, UUID> repository;

    public static void setup(final Repository<Reserva, UUID> repository) {
        ReservaCommandSupport.repository = repository;
    }

    @NonNull
    public static Repository<Reserva, UUID> getRepository() {
        return repository;
    }

    private ReservaCommandSupport() {
        // Vacío
    }
}
