package com.biopix.telemedicine_backend.marketplace.internal.dto;

public record CreateConsultationRequestResponse(
        ConsultationRequestResponse request,
        String setupUrl
) {
}
