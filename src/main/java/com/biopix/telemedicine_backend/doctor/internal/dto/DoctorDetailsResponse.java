package com.biopix.telemedicine_backend.doctor.internal.dto;

import com.biopix.telemedicine_backend.doctor.internal.entity.Speciality;


record DoctorDetailsResponse(
        Long id,
        Speciality  speciality,
        String licenseNumber,
        String clinicAddress
)
{}
