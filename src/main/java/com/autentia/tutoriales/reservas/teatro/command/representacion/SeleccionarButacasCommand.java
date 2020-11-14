package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.Command;
import com.autentia.tutoriales.reservas.teatro.Event;
import lombok.Value;

import java.util.List;
import java.util.Set;

@Value
public class SeleccionarButacasCommand implements Command<Representacion> {

    Set<Butaca> butacas;

    public boolean isValid(Representacion root) {
        return root.getButacasLibres().containsAll(butacas);
    }

    @SuppressWarnings("java:S1172")
    public List<Event> execute(Representacion root) {
        return List.of(new ButacasSeleccionadasEvent(root.getId(), butacas));
    }

    public void committed(Representacion root) {
        // Nada
    }

    public void rolledBack(Representacion root) {
        // Nada
    }
}
