package com.academy.orders.domain.passwordreset.exception;

public class TokenNotFoundException extends RuntimeException {
  public TokenNotFoundException(String token) {
    super(token);
  }
}
