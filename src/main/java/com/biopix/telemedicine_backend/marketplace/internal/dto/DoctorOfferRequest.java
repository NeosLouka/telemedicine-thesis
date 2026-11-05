package com.biopix.telemedicine_backend.marketplace.internal.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record DoctorOfferRequest(
     Long requestId,
     BigDecimal price,
     Instant proposedStartTime,
     Integer estimatedDurationMinutes,
     String message
) {
}
