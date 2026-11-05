package com.biopix.telemedicine_backend.shared.contracts.events;

public record SuccessfulLoginEvent(
        Long userId,
        String userEmail,
        String userRole
) {
}
