package com.biopix.telemedicine_backend.shared.contracts.events;

import java.util.List;

public record DoctorsToNotify(
        List<Long> onlineDoctorsToNotify,
        String description,
        Long requestId,
        String type

) {
}
