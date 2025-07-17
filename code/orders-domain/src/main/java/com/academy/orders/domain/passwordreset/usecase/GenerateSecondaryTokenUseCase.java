package com.academy.orders.domain.passwordreset.usecase;

import com.academy.orders.domain.passwordreset.exception.InvalidTokenException;
import com.academy.orders.domain.passwordreset.exception.TokenNotFoundException;

/**
  * Use case for generating a secondary password reset token from a primary token.
  * The secondary token is used in the final step of the password reset process.
 */
public interface GenerateSecondaryTokenUseCase {
  /**
      * Generates a secondary token based on the provided primary token.
      * The primary token is marked as used during this process.
      *
      * @param primaryToken the primary token to validate and use for secondary token generation
      * @return the generated secondary token
      * @throws InvalidTokenException if the primary token is invalid or expired
      * @throws TokenNotFoundException if the primary token is not found
      */
  String generateSecondaryToken(String primaryToken);
}
