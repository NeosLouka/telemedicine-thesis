package com.biopix.telemedicine_backend.doctor.internal.exceptions;

import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentType;

public class DocumentFileMissingException extends RuntimeException {

    public DocumentFileMissingException(DocumentType docType) {
        super("Document exists but no file has been uploaded for document type: " + docType);
    }
}