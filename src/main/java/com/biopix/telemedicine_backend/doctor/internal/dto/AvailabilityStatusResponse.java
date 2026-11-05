package com.biopix.telemedicine_backend.doctor.internal.dto;

import com.biopix.telemedicine_backend.doctor.internal.enums.DoctorAvailabilityStatus;

public record AvailabilityStatusResponse(
        Long doctorId,
        DoctorAvailabilityStatus status
){}
