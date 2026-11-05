package com.biopix.telemedicine_backend.auth.internal.dto;

import com.biopix.telemedicine_backend.auth.internal.enums.AccountStatus;
import com.biopix.telemedicine_backend.auth.internal.enums.AuthRole;
import com.biopix.telemedicine_backend.marketplace.internal.dto.AcceptedOfferResponse;

public record LoginResponse(
        String accessToken,
        Long userId,
        String email,
        AuthRole role,
        AccountStatus accountStatus
){}
