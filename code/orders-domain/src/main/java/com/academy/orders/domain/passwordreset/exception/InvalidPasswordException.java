package com.academy.orders.domain.passwordreset.exception;

public class InvalidPasswordException extends RuntimeException {
  /**
     * Constructs a new InvalidPasswordException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
  public InvalidPasswordException(String message) {
    super(message);
  }
}
