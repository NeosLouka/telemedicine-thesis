package com.biopix.telemedicine_backend.admin.internal.exceptions;

public class AdminProfileAlreadyExistsException extends RuntimeException {

    public AdminProfileAlreadyExistsException() {
        super("Admin profile already exists");
    }
}