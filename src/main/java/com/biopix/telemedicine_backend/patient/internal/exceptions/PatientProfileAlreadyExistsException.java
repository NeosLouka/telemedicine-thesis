package com.biopix.telemedicine_backend.patient.internal.exceptions;

public class PatientProfileAlreadyExistsException extends RuntimeException {

  public PatientProfileAlreadyExistsException() {
    super("Patient profile already exists");
  }
}