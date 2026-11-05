package com.biopix.telemedicine_backend.marketplace.internal.exceptions;

public class DoctorOfferNotFoundException extends RuntimeException {

  public DoctorOfferNotFoundException() {
    super("Doctor offer was not found");
  }
}