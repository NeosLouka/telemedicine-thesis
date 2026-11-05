package com.biopix.telemedicine_backend.marketplace.internal.exceptions;

public class UnauthorizedConsultationRequestAccessException extends RuntimeException {

    public UnauthorizedConsultationRequestAccessException() {
        super("You are not authorized to access this consultation request");
    }
}