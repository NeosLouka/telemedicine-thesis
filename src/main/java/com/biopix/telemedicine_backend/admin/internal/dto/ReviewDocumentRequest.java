package com.biopix.telemedicine_backend.admin.internal.dto;




import com.biopix.telemedicine_backend.admin.internal.enums.DocumentStatus;

import java.time.LocalDate;

public record ReviewDocumentRequest(
        DocumentStatus status,
        LocalDate expiryDate
) {
}
