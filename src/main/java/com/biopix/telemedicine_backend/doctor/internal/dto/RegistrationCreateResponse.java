package com.biopix.telemedicine_backend.doctor.internal.dto;


import com.biopix.telemedicine_backend.doctor.internal.enums.ReviewResult;

import java.time.Instant;

public record RegistrationCreateResponse(
        Long registrationId,
        Long doctorId,
        String  doctorEmail,
        ReviewResult reviewResult,
        Instant createdAt

) {
}
