package com.academy.orders.domain.passwordreset.exception;

public class InvalidEmailException extends RuntimeException {
  public InvalidEmailException(String message) {
    super(message);
  }
}
