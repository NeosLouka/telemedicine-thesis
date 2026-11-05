package com.biopix.telemedicine_backend.admin.internal.dto;

public record AdminInsertProfileDetailsResponse(
        Long adminId,
        String email,
        String firstName,
        String lastName,
        String department
) {
}
