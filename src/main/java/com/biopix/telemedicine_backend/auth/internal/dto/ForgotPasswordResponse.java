package com.biopix.telemedicine_backend.auth.internal.dto;

public record ForgotPasswordResponse(
        String message
) {
    public  ForgotPasswordResponse()
    {
        this("If an account exists, a reset code has been sent.");
    }
}
