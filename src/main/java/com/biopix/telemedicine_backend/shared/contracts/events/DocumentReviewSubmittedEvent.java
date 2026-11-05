package com.biopix.telemedicine_backend.shared.contracts.events;

import java.time.Instant;
import java.time.LocalDate;

public record DocumentReviewSubmittedEvent(
        Long documentId,
        Long adminId,
        String reviewStatus,
        LocalDate expiryDate,
        Instant reviewedAt
) {
}
