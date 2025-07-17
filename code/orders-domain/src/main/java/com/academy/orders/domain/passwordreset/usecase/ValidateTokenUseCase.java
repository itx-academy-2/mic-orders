package com.academy.orders.domain.passwordreset.usecase;

import com.academy.orders.domain.passwordreset.dto.TokenValidationResult;
import com.academy.orders.domain.passwordreset.exception.InvalidTokenException;

public interface ValidateTokenUseCase {
  /**
 * Validates the provided primary token and returns the result of the validation.
 *
 * @param token the primary token to be validated
 * @return the result of the token validation
 * @throws InvalidTokenException if the token is invalid or fails validation
 */
TokenValidationResult validatePrimaryToken(String token) throws InvalidTokenException;
}
