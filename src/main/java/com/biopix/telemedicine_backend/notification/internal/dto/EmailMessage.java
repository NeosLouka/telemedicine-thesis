package com.biopix.telemedicine_backend.notification.internal.dto;

public record EmailMessage(
        String to,
        String subject,
        String body
) {
}