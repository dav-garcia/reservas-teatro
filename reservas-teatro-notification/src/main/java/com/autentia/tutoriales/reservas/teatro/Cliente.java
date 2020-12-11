package com.autentia.tutoriales.reservas.teatro;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder")
public class Cliente implements AggregateRoot<String> {

    private final String id;
    private long version;
    private boolean suscrito;
    private final List<Descuento> descuentos;

    @NonNull
    public List<Descuento> getDescuentosAplicables(final LocalDate cuando, final int maximo) {
        final var aplicables = new ArrayList<Descuento>(descuentos.size());

        int restante = maximo;
        for (final Descuento descuento : descuentos) {
            if (descuento.getValidoDesde().compareTo(cuando) <= 0 &&
                    descuento.getValidoHasta().compareTo(cuando) >= 0 &&
                restante - descuento.getValor() >= 0) {
                aplicables.add(descuento);
                restante -= descuento.getValor();
            }
        }

        return aplicables;
    }

    @NonNull
    public List<Descuento> getDescuentosAplicados(final UUID reserva) {
        return descuentos.stream()
                .filter(d -> Objects.equals(d.getEnReserva(), reserva))
                .collect(Collectors.toList());
    }
}
