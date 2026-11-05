package com.biopix.telemedicine_backend.marketplace.internal.exceptions;

public class MissingSpecialityException extends RuntimeException {

    public MissingSpecialityException() {
        super("Speciality is required");
    }
}