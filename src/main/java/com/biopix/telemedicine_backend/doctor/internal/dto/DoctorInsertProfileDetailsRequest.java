package com.biopix.telemedicine_backend.doctor.internal.dto;

import java.time.LocalDate;

public record DoctorInsertProfileDetailsRequest(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String phone,
        String gender,
        String address,
        String country,
        String licenseNumber,
        String clinicAddress,
        String clinicPhone,
        String speciality
) {
}
