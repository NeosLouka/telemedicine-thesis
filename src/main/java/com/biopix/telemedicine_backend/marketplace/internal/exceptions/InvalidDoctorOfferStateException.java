package com.biopix.telemedicine_backend.marketplace.internal.exceptions;

public class InvalidDoctorOfferStateException extends RuntimeException {

    public InvalidDoctorOfferStateException(String message) {
        super(message);
    }
}