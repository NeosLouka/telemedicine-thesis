package com.biopix.telemedicine_backend.doctor.internal.dto;


import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentStatus;

import java.time.Instant;

public record DocumentUploadResponse(
        Long documentId,
        String documentType,
        DocumentStatus documentStatus,
        Instant createdAt
){}
