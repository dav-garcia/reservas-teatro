package com.autentia.tutoriales.reservas.teatro.infra.repository;

import com.autentia.tutoriales.reservas.teatro.infra.Entity;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Repositorio persistente de las proyecciones de las agregadas
 */
public interface Repository<T extends Entity<U>, U> {

    /**
     * Almacena una instancia (nueva o modificada)
     *
     * @param instance Instancia a guardar
     */
    void save(final T instance);

    /**
     * Carga una instancia, si existe
     *
     * @param id Identificador
     * @return Instancia o vacío si no existe
     */
    @NonNull
    Optional<T> load(final U id);

    /**
     * Encuentra las instancias que cumplen un filtro
     *
     * @param filter filtro que deben cumplir las instancias
     * @return instancias que cumplen el filtro
     */
    @NonNull
    List<T> find(final Predicate<T> filter);

    /**
     * Elimina una instancia
     *
     * @param id Identificador
     */
    void delete(final U id);
}
