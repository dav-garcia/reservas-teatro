package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder")
public class Cliente implements AggregateRoot<String> {

    private final String id;
    private long version;
    private boolean suscrito;
    private String nombre;
    private final List<Descuento> descuentos;

    @NonNull
    public String getEmail() {
        return id;
    }
}
