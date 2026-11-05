package com.biopix.telemedicine_backend.shared.contracts.events;

import java.math.BigDecimal;
import java.time.Instant;

public record ConsultationCreatedEvent(
        Long consultationId,

        Long patientAccountId,
        Long doctorAccountId,

        String patientEmail,
        String doctorEmail,

        BigDecimal price,

        Instant scheduledStartTime
) {
}
