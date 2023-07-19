package com.abnamro.battleship.exception;

public class InvalidShipDataException extends RuntimeException {
    public InvalidShipDataException(final String message) {
        super(message);
    }
}
