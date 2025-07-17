package com.academy.orders.domain.passwordreset.exception;

public class InvalidEmailException extends RuntimeException {
  /**
     * Constructs a new InvalidEmailException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
  public InvalidEmailException(String message) {
    super(message);
  }
}
