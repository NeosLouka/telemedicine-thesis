package com.biopix.telemedicine_backend.doctor.internal.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.biopix.telemedicine_backend.doctor")
public class DoctorExceptionHandler {

    @ExceptionHandler(DoctorProfileAlreadyExistsException.class)
    public ResponseEntity<String> handleDoctorProfileAlreadyExists(DoctorProfileAlreadyExistsException ex) {

        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<String> handleDoctorNotFound(DoctorNotFoundException ex) {

        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(DocumentUploadException.class)
    public ResponseEntity<String> handleDocumentUpload(DocumentUploadException ex) {

        log.error(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler(RegistrationAlreadyApprovedException.class)
    public ResponseEntity<String> handleRegistrationAlreadyApproved(RegistrationAlreadyApprovedException ex) {
        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<String> handleDocumentNotFound(DocumentNotFoundException ex) {

        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(DocumentFileMissingException.class)
    public ResponseEntity<String> handleDocumentFileMissing(DocumentFileMissingException ex) {
        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(RegistrationAlreadyExistsException.class)
    public ResponseEntity<String> handleRegistrationAlreadyExists(RegistrationAlreadyExistsException ex) {
        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(RegistrationNotFoundException.class)
    public ResponseEntity<String> handleRegistrationNotFound(RegistrationNotFoundException ex) {
        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(DocumentAlreadyApprovedException.class)
    public ResponseEntity<String> handleDocumentAlreadyApproved(DocumentAlreadyApprovedException ex) {
        log.warn(ex.getMessage());

        return  ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }
}