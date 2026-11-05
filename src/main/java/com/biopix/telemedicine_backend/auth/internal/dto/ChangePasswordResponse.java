package com.biopix.telemedicine_backend.auth.internal.dto;

public record ChangePasswordResponse(
        boolean success,
        String message
) {
    public static ChangePasswordResponse valid() {
        return new ChangePasswordResponse(true, "Your password has been changed successfully.");
    }

    public static ChangePasswordResponse invalid(String reason) {
        return new ChangePasswordResponse(false, reason);
    }
}
