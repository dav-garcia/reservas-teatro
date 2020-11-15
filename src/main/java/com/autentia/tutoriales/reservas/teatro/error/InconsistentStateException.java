package com.autentia.tutoriales.reservas.teatro.error;

public class InconsistentStateException extends CommandException {

    public InconsistentStateException(String message) {
        super(message);
    }

    public InconsistentStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
