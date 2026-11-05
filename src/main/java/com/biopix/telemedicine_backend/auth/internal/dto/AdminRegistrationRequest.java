package com.biopix.telemedicine_backend.auth.internal.dto;

import com.biopix.telemedicine_backend.auth.internal.enums.AuthRole;

public record AdminRegistrationRequest(
        String email,
        String password,
        AuthRole role,
        String secretKey
) {
}
