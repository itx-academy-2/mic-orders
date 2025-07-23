package com.academy.orders.domain.ratelimit.usecase;

import com.academy.orders.domain.ratelimit.dto.RateLimitResult;

/**
 * Use case for checking rate limits based on IP and email.
 */
public interface CheckRateLimitUseCase {
  /**
   * Checks if a request is allowed based on IP and email rate limits.
   *
   * @param ipAddress the client IP address
   * @param email the email address
   * @return RateLimitResult containing allowance status and rate limit metadata
   */
  RateLimitResult checkRateLimit(String ipAddress, String email);
}
