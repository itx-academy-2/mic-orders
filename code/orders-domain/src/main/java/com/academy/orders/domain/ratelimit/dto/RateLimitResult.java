package com.academy.orders.domain.ratelimit.dto;

/**
 * Represents the result of a rate limit check, including allowance status and metadata.
 *
 * @param isAllowed whether the request is allowed
 * @param remainingIpAttempts remaining attempts for the IP address
 * @param ipResetTime Unix timestamp (seconds) when IP rate limit resets
 * @param remainingEmailAttempts remaining attempts for the email address
 * @param emailResetTime Unix timestamp (seconds) when email rate limit resets
 */
public record RateLimitResult(
    boolean isAllowed,
    int remainingIpAttempts,
    long ipResetTime,
    int remainingEmailAttempts,
    long emailResetTime) {
}
