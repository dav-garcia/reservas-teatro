package com.autentia.tutoriales.reservas.teatro.command.representacion;

import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

import java.util.List;
import java.util.Set;

@Value
public class SeleccionarButacasCommand implements Command<Representacion> {

    Set<Butaca> butacas;

    public boolean isValid(Representacion root) {
        return root.getButacasLibres().containsAll(butacas);
    }

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
