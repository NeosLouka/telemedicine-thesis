package com.biopix.telemedicine_backend.doctor.internal.dto;

import java.time.Instant;
import java.time.LocalDate;

public record DoctorFullDetailsResponse(
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
        String licenseNumber,
        String clinicAddress,
        String clinicPhone,
        String speciality,
        Instant createdAt,
        Instant updatedAt
) {
}
