package com.biopix.telemedicine_backend.auth.internal.dto;

public record LoginRequest(
        String email,
        String password
){}
