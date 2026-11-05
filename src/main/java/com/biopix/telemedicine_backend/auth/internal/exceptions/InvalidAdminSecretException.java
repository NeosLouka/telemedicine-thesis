package com.biopix.telemedicine_backend.auth.internal.exceptions;

public class InvalidAdminSecretException extends RuntimeException {

    public InvalidAdminSecretException() {
        super("Invalid admin secret key");
    }
}