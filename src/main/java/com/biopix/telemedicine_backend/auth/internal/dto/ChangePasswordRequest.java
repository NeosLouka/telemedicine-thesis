package com.biopix.telemedicine_backend.auth.internal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank @Email String email,
        String newPassword,
        String otpCode
){}
