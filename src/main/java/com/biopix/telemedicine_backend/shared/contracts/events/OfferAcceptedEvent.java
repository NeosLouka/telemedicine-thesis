package com.biopix.telemedicine_backend.shared.contracts.events;

import java.math.BigDecimal;
import java.time.Instant;

public record OfferAcceptedEvent(
        Long requestId,
        Long offerId,

        Long patientAccountId,
        Long doctorAccountId,

        String patientEmail,
        String doctorEmail,

        BigDecimal price,
        String requestType,
        Instant scheduledStartTime,
        Integer estimatedDurationMinutes,

        Instant acceptedAt
) {}
