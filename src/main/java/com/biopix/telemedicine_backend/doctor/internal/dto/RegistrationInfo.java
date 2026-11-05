package com.biopix.telemedicine_backend.doctor.internal.dto;



import com.biopix.telemedicine_backend.doctor.internal.enums.ReviewResult;

import java.time.Instant;

public record RegistrationInfo(
        Long registrationId,
        DoctorFullDetailsResponse doctorDetails,
        Instant reviewedAt,
        ReviewResult reviewResult,
        String reviewComment,
        Instant createdAt,
        Instant updatedAt
) {
}
