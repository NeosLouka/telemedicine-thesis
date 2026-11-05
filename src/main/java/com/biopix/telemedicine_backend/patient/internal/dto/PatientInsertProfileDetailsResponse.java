package com.biopix.telemedicine_backend.patient.internal.dto;


public record PatientInsertProfileDetailsResponse(
        Long patientId,
        String email,
        String firstName,
        String lastName
) {
}
