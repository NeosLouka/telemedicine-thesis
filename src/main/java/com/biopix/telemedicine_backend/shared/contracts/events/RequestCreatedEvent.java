package com.biopix.telemedicine_backend.shared.contracts.events;

import java.time.Instant;

public record RequestCreatedEvent(
        Long requestId,
        Long specialityId,
        String requestType,
        Instant createdAt
){}
