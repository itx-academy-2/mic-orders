package com.academy.orders.domain.passwordreset.usecase;

import com.academy.orders.domain.passwordreset.dto.TokenValidationResult;
import com.academy.orders.domain.passwordreset.exception.InvalidTokenException;

public interface ValidateTokenUseCase {
  TokenValidationResult validatePrimaryToken(String token) throws InvalidTokenException;
}
