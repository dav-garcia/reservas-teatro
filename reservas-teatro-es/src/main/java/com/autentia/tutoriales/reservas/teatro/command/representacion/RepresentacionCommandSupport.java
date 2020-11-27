package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import org.springframework.lang.NonNull;

import java.util.UUID;

/**
 * Para que los Command sean auténticos POJO, necesitan un apoyo para
 * poder acceder a los servicios de infraestructura
 */
public class RepresentacionCommandSupport {

    private static Repository<Representacion, UUID> repository;

    public static void setup(final Repository<Representacion, UUID> repository) {
        RepresentacionCommandSupport.repository = repository;
    }

    @NonNull
    public static Repository<Representacion, UUID> getRepository() {
        return repository;
    }

    private RepresentacionCommandSupport() {
        // Vacío
    }
}
