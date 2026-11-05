package com.biopix.telemedicine_backend.patient.internal.exceptions;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException() {
        super("Patient profile was not found");
    }
}