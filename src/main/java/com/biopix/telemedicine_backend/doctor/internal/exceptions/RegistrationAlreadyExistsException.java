package com.biopix.telemedicine_backend.doctor.internal.exceptions;

public class RegistrationAlreadyExistsException extends RuntimeException {

    public RegistrationAlreadyExistsException() {
        super("Doctor registration already exists");
    }
}