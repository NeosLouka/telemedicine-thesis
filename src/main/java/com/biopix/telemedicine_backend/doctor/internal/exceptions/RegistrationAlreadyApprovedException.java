package com.biopix.telemedicine_backend.doctor.internal.exceptions;

public class RegistrationAlreadyApprovedException extends RuntimeException {

    public RegistrationAlreadyApprovedException() {
        super("Cannot upload documents after registration approval");
    }
}