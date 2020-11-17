package com.autentia.tutoriales.reservas.teatro.error;

public class InconsistentStateException extends EventStreamException {

    public InconsistentStateException(final String message) {
        super(message);
    }

    public InconsistentStateException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
