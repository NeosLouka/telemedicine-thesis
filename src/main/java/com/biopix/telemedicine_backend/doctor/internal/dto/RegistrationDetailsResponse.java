package com.biopix.telemedicine_backend.doctor.internal.dto;



import com.biopix.telemedicine_backend.doctor.internal.enums.ReviewResult;

import java.time.Instant;

public record RegistrationDetailsResponse(
        Long registrationId,
        String  doctorEmail,
        Instant reviewedAt,
        Long reviewedByAdminId,
        ReviewResult reviewResult,
        String reviewComment,
        Instant createdAt,
        Instant updatedAt
){}
