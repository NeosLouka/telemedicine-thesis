package com.biopix.telemedicine_backend.marketplace.internal.exceptions;

public class ConsultationRequestNotFoundException extends RuntimeException {

    public ConsultationRequestNotFoundException() {
        super("Consultation request was not found");
    }
}
