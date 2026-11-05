package com.biopix.telemedicine_backend.patient.internal.dto;

import com.biopix.telemedicine_backend.patient.internal.enums.PatientAccountStatus;

public record PatientBasicDetailsResponse(
        Long patientId,
        String email,
        String firstName,
        String lastName
) {
}
