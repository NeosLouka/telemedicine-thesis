package com.biopix.telemedicine_backend.doctor.internal.exceptions;

public class DoctorProfileAlreadyExistsException extends RuntimeException {

    public DoctorProfileAlreadyExistsException() {
        super("Doctor profile already exists");
    }
}