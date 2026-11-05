package com.biopix.telemedicine_backend.video.internal.twilio.dto;

public record JoinVideoResponse(
        String roomName,
        String accessToken
) {
}