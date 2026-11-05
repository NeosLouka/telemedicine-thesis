package com.biopix.telemedicine_backend.marketplace.internal.dto;

import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestStatus;
import com.biopix.telemedicine_backend.marketplace.internal.enums.DoctorOfferStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record AcceptedOfferResponse(
        Long requestId,
        Long offerId,
        String patientEmail,
        String doctorEmail,
        BigDecimal price,
        Instant consultationStartTime,
        Integer estimatedDurationMinutes,
        DoctorOfferStatus offerStatus,
        ConsultationRequestStatus requestStatus,
        Instant fulfilledAt
) {
}