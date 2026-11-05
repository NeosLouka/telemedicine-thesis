package com.biopix.telemedicine_backend.shared.contracts.events;

public record ProfileCompletedEvent(
        Long accountId,
        String email,
        String role
) {
    public ProfileCompletedEvent(Long accountId,String role) {
        this(accountId,null,role);
    }
}
