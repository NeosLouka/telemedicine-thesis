package com.biopix.telemedicine_backend.marketplace.internal.dto;

import com.biopix.telemedicine_backend.marketplace.internal.enums.DoctorOfferStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record DoctorOfferResponse(
        Long offerId,
        Long requestId,
        BigDecimal price,
        Instant proposedStartTime,
        DoctorOfferStatus status,
        Instant createdAt
) {
}
