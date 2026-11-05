package com.biopix.telemedicine_backend.shared.contracts.events;


public record SuccessfulLogoutEvent(
        Long id,
        String email,
        String role,
        String fcmToken
){
    public SuccessfulLogoutEvent(Long id, String email, String role){
        this(id, email, role, null);
    }
}
