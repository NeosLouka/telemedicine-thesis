package com.biopix.telemedicine_backend.shared.contracts.events;

import java.time.Instant;

public record RegistrationReviewSubmittedEvent(
        Long registrationId,
        Long adminId,
        String reviewResult,
        String reviewComment,
        Instant reviewedAt
){}
