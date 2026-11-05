package com.biopix.telemedicine_backend.marketplace.internal.exceptions;

public class DoctorOfferAlreadyExistsException extends RuntimeException {

    public DoctorOfferAlreadyExistsException() {
        super("Doctor offer already exists");
    }
}