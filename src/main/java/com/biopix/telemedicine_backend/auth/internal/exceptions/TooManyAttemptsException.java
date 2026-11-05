package com.biopix.telemedicine_backend.auth.internal.exceptions;

public class TooManyAttemptsException extends RuntimeException {
    public TooManyAttemptsException() {
        super("Too many attempts. Please try again later.");
    }

    public TooManyAttemptsException(String message) {
        super(message);
    }
}
