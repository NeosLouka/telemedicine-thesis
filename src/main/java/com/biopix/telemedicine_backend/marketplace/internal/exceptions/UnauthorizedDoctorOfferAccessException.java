package com.biopix.telemedicine_backend.marketplace.internal.exceptions;

public class UnauthorizedDoctorOfferAccessException extends RuntimeException {

    public UnauthorizedDoctorOfferAccessException(String message) {
        super(message);
    }
}