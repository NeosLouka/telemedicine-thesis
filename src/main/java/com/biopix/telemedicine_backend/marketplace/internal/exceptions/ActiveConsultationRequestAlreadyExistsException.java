package com.biopix.telemedicine_backend.marketplace.internal.exceptions;

public class ActiveConsultationRequestAlreadyExistsException extends RuntimeException {

    public ActiveConsultationRequestAlreadyExistsException() {
        super("Patient already has an open consultation request");
    }
}