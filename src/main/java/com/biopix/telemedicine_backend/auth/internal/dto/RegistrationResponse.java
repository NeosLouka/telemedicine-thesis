package com.biopix.telemedicine_backend.auth.internal.dto;

import com.biopix.telemedicine_backend.auth.internal.entity.AuthAccount;
import com.biopix.telemedicine_backend.auth.internal.enums.AccountStatus;
import com.biopix.telemedicine_backend.auth.internal.enums.AuthRole;

public record RegistrationResponse(
        String accessToken,
        Long accountId,
        String email,
        AuthRole role,
        AccountStatus status
){}
