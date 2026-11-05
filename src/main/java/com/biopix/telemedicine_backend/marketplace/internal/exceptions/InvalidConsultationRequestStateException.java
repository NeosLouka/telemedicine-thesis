package com.biopix.telemedicine_backend.marketplace.internal.exceptions;

public class InvalidConsultationRequestStateException extends RuntimeException {
    public InvalidConsultationRequestStateException(String message) {
        super(message);
    }
}
