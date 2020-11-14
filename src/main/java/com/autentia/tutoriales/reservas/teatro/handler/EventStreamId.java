package com.autentia.tutoriales.reservas.teatro.handler;

import com.autentia.tutoriales.reservas.teatro.AggregateRoot;
import lombok.Value;

@Value
public class EventStreamId<T> {

    Class<? extends AggregateRoot<T>> type;
    T id;
}
