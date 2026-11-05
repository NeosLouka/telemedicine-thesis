package com.biopix.telemedicine_backend.admin.internal.exceptions;

public class AdminNotFoundException extends RuntimeException {

    public AdminNotFoundException() {
        super("Admin profile was not found");
    }
}