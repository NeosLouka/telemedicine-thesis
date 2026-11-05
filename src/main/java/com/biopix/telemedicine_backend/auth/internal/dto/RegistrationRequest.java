package com.biopix.telemedicine_backend.auth.internal.dto;

import com.biopix.telemedicine_backend.auth.internal.enums.AuthRole;

public record RegistrationRequest(
        String email,
        String password,
        AuthRole role
){}
