package com.academy.orders.domain.passwordreset.exception;

/**
  * Exception thrown when a password reset token is invalid.
  * This can occur when the token has expired, has been used, or is malformed.
  */
public class InvalidTokenException extends RuntimeException {
  /**
   * Creates a new InvalidTokenException with the specified message.
   *
   * @param message the detail message explaining why the token is invalid
   * */
  public InvalidTokenException(String message) {
    super(message);
  }
}
