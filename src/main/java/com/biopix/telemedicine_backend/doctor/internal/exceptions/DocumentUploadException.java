package com.biopix.telemedicine_backend.doctor.internal.exceptions;

public class DocumentUploadException extends RuntimeException {

    public DocumentUploadException(String message) {
        super(message);
    }

    public DocumentUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}