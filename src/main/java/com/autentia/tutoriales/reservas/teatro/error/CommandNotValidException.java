package com.autentia.tutoriales.reservas.teatro.error;

public class CommandNotValidException extends CommandException {

    public CommandNotValidException(final String message) {
        super(message);
    }

    public CommandNotValidException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
