package com.biopix.telemedicine_backend.shared.contracts.events;

public record DoctorRegistrationApprovedEvent(
        Long accountId,
        Long doctorId,
        Long registrationId,
        String email
) {
    public DoctorRegistrationApprovedEvent(Long accountId, Long doctorId,Long registrationId) {
        this(accountId,doctorId,registrationId,null);
    }
}