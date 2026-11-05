package com.biopix.telemedicine_backend.notification.internal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterFcmTokenRequest(
        @NotBlank(message = "FCM token must not be blank")
        @Size(max = 500, message = "FCM token string is too long")
        String fcmToken,

        @Size(max = 32, message = "Device type string is too long")
        String deviceType

) {
}
