package com.academy.orders.domain.passwordreset.exception;

/**
  * Exception thrown when an email address is invalid during password reset operations.
  * This typically occurs during token creation when the provided email doesn't exist
  * in the system or doesn't meet validation requirements.
  */
public class InvalidEmailException extends RuntimeException {
  /**
   * Creates a new InvalidEmailException with the specified message.
   *
   * @param message the detail message explaining why the email is invalid
   */
  public InvalidEmailException(String message) {
    super(message);
  }
}
