package com.biopix.telemedicine_backend.auth.internal.dto;

public record LogoutRequest(
        String fcmToken
) {
}
