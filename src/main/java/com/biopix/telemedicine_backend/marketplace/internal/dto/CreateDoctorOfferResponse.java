package com.biopix.telemedicine_backend.marketplace.internal.dto;

public record CreateDoctorOfferResponse(
        DoctorOfferResponse offer,
        String setupUrl
) {
}
