package com.biopix.telemedicine_backend.patient.internal.dto;

import java.time.LocalDate;

public record PatientInsertProfileDetailsRequest(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String phone,
        String gender,
        String address,
        String country
) {
}
