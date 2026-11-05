package com.biopix.telemedicine_backend.auth.internal.exceptions;

public class InvalidRegistrationRoleException extends RuntimeException {

  public InvalidRegistrationRoleException(String message) {
    super(message);
  }
}