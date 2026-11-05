package com.biopix.telemedicine_backend.notification.internal.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationTitle {

    NEW_PATIENT_REQUEST("New Marketplace Request.", "MARKETPLACE_REQUEST"),
    DOCTOR_REVIEWED("Doctor Account Reviewed", "ACCOUNT_STATUS"),
    SCHEDULED_CONSULTATION( "Scheduled consultation", "SCHEDULED_CONSULTATION"),;

    private final String title;
    private final String defaultType;
}