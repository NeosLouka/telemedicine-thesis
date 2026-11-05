package com.biopix.telemedicine_backend.doctor.internal.dto;


import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentType;

public record DocumentUrlResponse(
        Long documentId,
        Long registrationId,
        DocumentType documentType,
        String url,
        Long urlExpiresIn
)
{}
