package com.biopix.telemedicine_backend.doctor.internal.dto;


import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentStatus;
import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentType;

import java.time.Instant;
import java.time.LocalDate;

public record DocumentDetails(
        Long id,
        DocumentType docType,
        String downloadUrl,
        DocumentStatus status,
        LocalDate expiryDate,
        Instant createdAt,
        Instant updatedAt
) {
}
