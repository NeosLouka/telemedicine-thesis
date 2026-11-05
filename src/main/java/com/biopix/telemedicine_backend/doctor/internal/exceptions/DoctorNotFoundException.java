package com.biopix.telemedicine_backend.doctor.internal.exceptions;

public class DoctorNotFoundException extends RuntimeException {

    public DoctorNotFoundException() {
        super("Doctor profile was not found");
    }
}
