package com.autentia.tutoriales.reservas.teatro.error;

public class EventStreamException extends RuntimeException {

    public EventStreamException(final String message) {
        super(message);
    }

    public EventStreamException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
