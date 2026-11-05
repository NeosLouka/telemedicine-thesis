package com.biopix.telemedicine_backend.admin.internal.dto;


import com.biopix.telemedicine_backend.admin.internal.enums.ReviewResult;

public record ReviewRegistrationRequest(
        ReviewResult result,
        String comment
) {}
