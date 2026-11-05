package com.biopix.telemedicine_backend.doctor.internal.exceptions;

public class DocumentAlreadyApprovedException extends RuntimeException {
    public DocumentAlreadyApprovedException(String message) {
        super(message);
    }
}
