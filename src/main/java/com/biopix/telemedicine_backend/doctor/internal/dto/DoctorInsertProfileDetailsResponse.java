package com.biopix.telemedicine_backend.doctor.internal.dto;


public record DoctorInsertProfileDetailsResponse(
        Long doctorId,
        String email,
        String firstName,
        String lastName
){}
