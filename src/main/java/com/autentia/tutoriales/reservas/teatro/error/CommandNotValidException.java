package com.autentia.tutoriales.reservas.teatro.error;

public class CommandNotValidException extends CommandException {

    public CommandNotValidException(String message) {
        super(message);
    }


    public CommandNotValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
