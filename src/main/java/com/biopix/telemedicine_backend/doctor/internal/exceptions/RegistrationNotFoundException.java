package com.biopix.telemedicine_backend.doctor.internal.exceptions;

public class RegistrationNotFoundException extends RuntimeException {

    public RegistrationNotFoundException() {
        super("Doctor registration was not found");
    }
}