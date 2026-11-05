package com.biopix.telemedicine_backend.consultation.internal.dto;

import com.biopix.telemedicine_backend.consultation.internal.entity.Consultation;
import com.biopix.telemedicine_backend.consultation.internal.enums.ConsultationStatus;
import com.biopix.telemedicine_backend.consultation.internal.enums.NoShowReason;
import com.biopix.telemedicine_backend.consultation.internal.enums.RequestType;

import java.math.BigDecimal;
import java.time.Instant;

public record ConsultationResponse(
        Long consultationId,
        BigDecimal price,
        RequestType type,
        Instant scheduledStartTime,
        ConsultationStatus status,
        ConsultationMetadata metadata
) {
    public record ConsultationMetadata(
            Instant readyAt,
            Instant cancelledAt,
            Instant completedAt,
            Instant noShowMarkedAt,
            NoShowReason noShowReason,
            String failureCode,
            String failureReason
    ) {}

    public static ConsultationResponse fromEntity(Consultation entity) {
        return new ConsultationResponse(
                entity.getId(),
                entity.getPrice(),
                entity.getRequestType(),
                entity.getScheduledStartTime(),
                entity.getStatus(),
                new ConsultationMetadata(
                        entity.getReadyAt(),
                        entity.getCancelledAt(),
                        entity.getCompletedAt(),
                        entity.getNoShowMarkedAt(),
                        entity.getNoShowReason(),
                        entity.getFailureCode(),
                        entity.getFailureReason()
                )
        );
    }
}
