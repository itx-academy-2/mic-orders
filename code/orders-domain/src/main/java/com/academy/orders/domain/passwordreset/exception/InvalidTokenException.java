package com.academy.orders.domain.passwordreset.exception;

public class InvalidTokenException extends RuntimeException {
  /**
     * Constructs a new InvalidTokenException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the invalid token exception
     */
  public InvalidTokenException(String message) {
    super(message);
  }
}
