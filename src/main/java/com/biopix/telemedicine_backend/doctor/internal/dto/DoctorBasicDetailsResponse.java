package com.biopix.telemedicine_backend.doctor.internal.dto;

public record DoctorBasicDetailsResponse(
        Long doctorId,
        String email,
        String firstName,
        String lastName,
        String clinicAddress,
        String availabilityStatus

){}
