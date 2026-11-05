package com.biopix.telemedicine_backend.video.internal.twilio.dto;

import com.biopix.telemedicine_backend.video.internal.twilio.enums.VideoSessionStatus;

import java.time.Instant;

public record VideoSessionStatusResponse(
        Long consultationId,
        String roomName,
        VideoSessionStatus status,
        Instant endedAt
) {}
