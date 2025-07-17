package com.academy.orders.domain.passwordreset.exception;

/**
 * Exception thrown when a password reset token is not found in the system.
 * This typically occurs when attempting to validate or use a non-existent token.
 */
public class TokenNotFoundException extends RuntimeException {
  /**
   * Creates a new TokenNotFoundException.
   *
   * @param token the token that was not found (used for internal tracking only)
   */
  public TokenNotFoundException(String token) {
    super(token);
  }
}
