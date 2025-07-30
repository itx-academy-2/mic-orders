package com.academy.orders.domain.passwordreset.usecase;

import com.academy.orders.domain.passwordreset.dto.TokenValidationResult;
import com.academy.orders.domain.passwordreset.exception.InvalidTokenException;

/**
 * Use case interface for validating password reset tokens. <p> Primarily used to verify the authenticity and validity of a primary token
 * before proceeding with the next phase of the password reset process.
 */
public interface ValidateTokenUseCase {

  /**
   * Validates the given primary password reset token. <p> This includes checks such as whether the token exists, has not expired, is not
   * already used, and belongs to a valid account.
   *
   * @param token the primary reset token to validate
   * @return {@link TokenValidationResult} containing details about the validation outcome
   * @throws InvalidTokenException if the token is malformed, expired, used, or otherwise invalid
   */
  TokenValidationResult validatePrimaryToken(String token) throws InvalidTokenException;
}
