package com.biopix.telemedicine_backend.marketplace.internal.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.biopix.telemedicine_backend.marketplace")
public class MarketplaceExceptionHandler {

    @ExceptionHandler(ActiveConsultationRequestAlreadyExistsException.class)
    public ResponseEntity<String> handleActiveRequestAlreadyExists(
            ActiveConsultationRequestAlreadyExistsException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ConsultationRequestNotFoundException.class)
    public ResponseEntity<String> handleConsultationRequestNotFound(
            ConsultationRequestNotFoundException ex
    ) {
        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(MissingSpecialityException.class)
    public ResponseEntity<String> handleMissingSpeciality(
            MissingSpecialityException ex
    ) {
        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedConsultationRequestAccessException.class)
    public ResponseEntity<String> handleUnauthorizedAccess(
            UnauthorizedConsultationRequestAccessException ex
    ) {
        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidConsultationRequestStateException.class)
    public ResponseEntity<String> handleInvalidRequestState(
            InvalidConsultationRequestStateException ex
    ) {
        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(DoctorOfferAlreadyExistsException.class)
    public ResponseEntity<String> handleDoctorOfferAlreadyExists(DoctorOfferAlreadyExistsException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(DoctorOfferNotFoundException.class)
    public ResponseEntity<String> handleDoctorOfferNotFound(DoctorOfferNotFoundException ex) {

        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedDoctorOfferAccessException.class)
    public ResponseEntity<String> handleUnauthorizedDoctorOfferAccess(UnauthorizedDoctorOfferAccessException ex) {
        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidDoctorOfferStateException.class)
    public ResponseEntity<String> handleInvalidDoctorOfferState(InvalidDoctorOfferStateException ex) {
        log.warn(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidProposedTimeException.class)
    public ResponseEntity<String> handleInvalidProposedTime(InvalidProposedTimeException ex) {
        log.warn(ex.getMessage());

        return  ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}