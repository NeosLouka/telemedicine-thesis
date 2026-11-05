package com.biopix.telemedicine_backend.auth.internal.service.OTPcache.dto;

public record OtpData(
        String codeHash,
        int attempts
) {
    public  OtpData(String codeHash) {
        this(codeHash, 0);
    }
}
