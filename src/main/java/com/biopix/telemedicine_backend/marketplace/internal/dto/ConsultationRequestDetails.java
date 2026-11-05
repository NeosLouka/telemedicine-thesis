package com.biopix.telemedicine_backend.marketplace.internal.dto;

import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestStatus;
import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestType;

import java.time.Instant;

public record ConsultationRequestDetails(
        Long consultationRequestId,
        Long specialityId,
        String issueDescription,
        ConsultationRequestType requestType,
        Instant preferredStartTime,
        ConsultationRequestStatus status,
        Instant expiresAt,
        Instant fulfilledAt,
        Instant cancelledAt,
        Instant createdAt,
        Instant updatedAt
) {
}
