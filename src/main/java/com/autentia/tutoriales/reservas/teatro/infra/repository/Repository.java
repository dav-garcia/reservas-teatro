package com.autentia.tutoriales.reservas.teatro.infra.repository;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * Repositorio persistente de las proyecciones de las agregadas
 */
public interface Repository<T extends AggregateRoot<U>, U> {

    /**
     * Carga una instancia, si existe
     *
     * @param id Identificador
     * @return Instancia o vacío si no existe
     */
    @NonNull
    Optional<T> load(final U id);

    /**
     * Almacena una instancia (nueva o modificada)
     *
     * @param instance Instancia a guardar
     */
    void save(final T instance);

    /**
     * Elimina una instancia
     *
     * @param id Identificador
     */
    void delete(final U id);
}
