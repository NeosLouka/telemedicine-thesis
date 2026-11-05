package com.biopix.telemedicine_backend.doctor.internal.dto;

public record SpecialityResponse(
        Long id,
        String name,
        String description
) {}