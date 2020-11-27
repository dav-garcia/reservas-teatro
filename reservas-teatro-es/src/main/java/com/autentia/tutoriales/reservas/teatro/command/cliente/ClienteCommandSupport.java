package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import org.springframework.lang.NonNull;

/**
 * Para que los Command sean auténticos POJO, necesitan un apoyo para
 * poder acceder a los servicios de infraestructura
 */
public class ClienteCommandSupport {

    private static Repository<Cliente, String> repository;

    public static void setup(final Repository<Cliente, String> repository) {
        ClienteCommandSupport.repository = repository;
    }

    @NonNull
    public static Repository<Cliente, String> getRepository() {
        return repository;
    }

    private ClienteCommandSupport() {
        // Vacío
    }
}
