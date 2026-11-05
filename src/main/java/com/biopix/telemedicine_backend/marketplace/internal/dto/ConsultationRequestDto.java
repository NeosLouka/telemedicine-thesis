package com.biopix.telemedicine_backend.marketplace.internal.dto;

import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestType;

import java.time.Instant;

public record ConsultationRequestDto(
        Long specialityId,
        String issueDescription,
        ConsultationRequestType requestType,
        Instant preferredStartTime
){}
