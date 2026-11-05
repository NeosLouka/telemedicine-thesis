package com.biopix.telemedicine_backend.marketplace.internal.dto;

import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestType;
import com.biopix.telemedicine_backend.marketplace.internal.enums.SortByStatus;

public record ConsultationRequestFilterDetails(
        Long specialityId,
        ConsultationRequestType requestType,
        SortByStatus sortBy// "newest"/"oldest"
) {
}
