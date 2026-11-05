package com.biopix.telemedicine_backend.admin.internal.dto;

import java.time.Instant;

public record AdminFullDetailsResponse(
        Long adminId,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String department,
        Instant createdAt,
        Instant updatedAt
) {
}
