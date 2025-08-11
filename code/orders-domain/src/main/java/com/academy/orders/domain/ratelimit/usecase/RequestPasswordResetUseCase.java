package com.academy.orders.domain.ratelimit.usecase;

import com.academy.orders.domain.passwordreset.dto.PasswordResetEmailCommand;
import com.academy.orders.domain.ratelimit.dto.RateLimitResult;

/**
 * Use case for requesting a password reset.
 */
public interface RequestPasswordResetUseCase {
  /**
   * Requests a password reset for the given email.
   *
   * @param command the password reset command
   * @return RateLimitResult containing allowance status and rate limit metadata
   */
  RateLimitResult requestPasswordReset(PasswordResetEmailCommand command);
}
