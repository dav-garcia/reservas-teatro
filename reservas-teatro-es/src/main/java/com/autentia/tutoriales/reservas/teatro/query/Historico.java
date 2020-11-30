package com.autentia.tutoriales.reservas.teatro.query;

import com.autentia.tutoriales.reservas.teatro.event.pago.Concepto;
import com.autentia.tutoriales.reservas.teatro.event.representacion.Butaca;
import com.autentia.tutoriales.reservas.teatro.infra.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder")
public class Historico implements Entity<String> {

    @Value
    public static class Descuento {
        UUID id;
        String descripcion;
        int valor;
        LocalDate validoDesde;
        LocalDate validoHasta;
    }

    @Getter
    @Setter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Builder(builderClassName = "Builder")
    public static class Reserva {

        public enum Estado {
            CREADA,
            CONFIRMADA,
            PAGADA,
            ABANDONADA,
            CANCELADA
        }

        private final UUID id;
        private final Set<Butaca> butacas;
        private final List<Descuento> descuentos;
        private UUID pago;
        private List<Concepto> conceptos;
        private Estado estado;
    }

    private final String id;
    private String nombre;
    private boolean suscrito;
    private final List<Descuento> descuentos;
    private Map<UUID, Reserva> reservas;

    public void aplicarDescuentos(final UUID idReserva, final List<UUID> idDescuentos) {
        final var reserva = reservas.get(idReserva);
        final var aplicados = descuentos.stream()
                .filter(d -> idDescuentos.contains(d.getId()))
                .collect(Collectors.toList());
        descuentos.removeAll(aplicados);
        reserva.descuentos.addAll(aplicados);
    }

    public void recuperarDescuentos(final UUID idReserva, final List<UUID> idDescuentos) {
        final var reserva = reservas.get(idReserva);
        final var recuperados = reserva.descuentos.stream()
                .filter(d -> idDescuentos.contains(d.getId()))
                .collect(Collectors.toList());
        reserva.descuentos.removeAll(recuperados);
        descuentos.addAll(recuperados);
    }

    public Reserva getReserva(final UUID idReserva) {
        return reservas.get(idReserva);
    }
}
