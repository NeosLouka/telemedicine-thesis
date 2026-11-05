package com.biopix.telemedicine_backend.marketplace.internal.dto;

import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestStatus;

import java.time.Instant;

public record ConsultationRequestResponse(
        Long id,
        String patientEmail,
        Instant preferredStartTime,
        ConsultationRequestStatus status,
        Instant createdAt

) {
}
