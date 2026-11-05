package com.biopix.telemedicine_backend.marketplace.internal.exceptions;

public class InvalidProposedTimeException extends RuntimeException {
    public InvalidProposedTimeException(String message) {
        super(message);
    }
}