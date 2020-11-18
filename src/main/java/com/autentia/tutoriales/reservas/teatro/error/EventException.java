package com.autentia.tutoriales.reservas.teatro.error;

public class EventException extends RuntimeException {

    public EventException(final String message) {
        super(message);
    }

    public EventException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
