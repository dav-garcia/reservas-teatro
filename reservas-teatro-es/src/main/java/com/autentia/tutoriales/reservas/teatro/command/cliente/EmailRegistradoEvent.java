package com.autentia.tutoriales.reservas.teatro.command.cliente;

import com.autentia.tutoriales.reservas.teatro.infra.Event;
import lombok.Value;

@Value
public class EmailRegistradoEvent implements Event<String> {

    String aggregateRootId;
}
