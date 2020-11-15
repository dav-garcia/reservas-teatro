package com.autentia.tutoriales.reservas.teatro.infra.handler;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import lombok.Value;

@Value
public class EventStreamId<T> {

    Class<? extends AggregateRoot<T>> type;
    T id;
}
