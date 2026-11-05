package com.biopix.telemedicine_backend.doctor.internal.exceptions;

import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentType;

public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(Long registrationId, DocumentType docType) {
        super("No document found for registration: " + registrationId + " and document type: " + docType);
    }

    public DocumentNotFoundException(String s) {
    }
}