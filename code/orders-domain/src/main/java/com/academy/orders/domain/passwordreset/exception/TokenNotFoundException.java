package com.academy.orders.domain.passwordreset.exception;

public class TokenNotFoundException extends RuntimeException {
  /**
   * Constructs a new TokenNotFoundException with the specified token as its detail message.
   *
   * @param token the token that was not found
   */
  public TokenNotFoundException(String token) {
    super(token);
  }
}
