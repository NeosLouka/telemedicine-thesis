package com.biopix.telemedicine_backend.auth.internal.exceptions;

public class InvalidOtpCodeException extends RuntimeException {
    public InvalidOtpCodeException(String message) {
        super(message);
    }
}
