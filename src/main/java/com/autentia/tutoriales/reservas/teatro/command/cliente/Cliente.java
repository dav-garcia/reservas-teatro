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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder")
public class Cliente implements AggregateRoot<String> {

    String id;
    @Setter
    long version;
    @Setter
    boolean suscrito;
    @Setter
    String nombre;
    List<Descuento> descuentos;

    @NonNull
    public String getEmail() {
        return id;
    }
}
