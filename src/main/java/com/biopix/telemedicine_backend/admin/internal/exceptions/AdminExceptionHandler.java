package com.biopix.telemedicine_backend.admin.internal.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.biopix.telemedicine_backend.admin")
public class AdminExceptionHandler {

    @ExceptionHandler(AdminProfileAlreadyExistsException.class)
    public ResponseEntity<String> handleAdminProfileAlreadyExists(AdminProfileAlreadyExistsException ex) {

        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(AdminNotFoundException.class)
    public ResponseEntity<String> handleAdminNotFound(AdminNotFoundException ex) {

        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidRegistrationReviewException.class)
    public ResponseEntity<String> handleInvalidRegistrationReview(InvalidRegistrationReviewException ex) {
        log.warn("Invalid registration review attempt: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
