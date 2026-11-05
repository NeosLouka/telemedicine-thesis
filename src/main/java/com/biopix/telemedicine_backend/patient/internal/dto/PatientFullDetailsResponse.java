package com.biopix.telemedicine_backend.patient.internal.dto;

import java.time.Instant;
import java.time.LocalDate;

public record PatientFullDetailsResponse(
        Long doctorId,
        String firstName,
        String lastName,
        String email,
        LocalDate dateOfBirth,
        String phone,
        String gender,
        String address,
        String country,
        String role,
        Instant createdAt,
        Instant updatedAt
){}
