package com.academy.orders.application.ratelimit.usecase;

import com.academy.orders.application.common.rate.RateLimitWindow;
import com.academy.orders.domain.ratelimit.dto.RateLimitResult;
import com.academy.orders.domain.ratelimit.usecase.CheckRateLimitUseCase;
import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Use case implementation for checking rate limits on requests by IP address and email. <p> This class tracks attempts per IP and per email
 * hash, enforcing limits of: <ul> <li>5 requests per minute per IP address</li> <li>3 requests per hour per email address</li> </ul> It
 * uses in-memory concurrent maps to track sliding time windows of attempts. </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitUseCaseImpl implements CheckRateLimitUseCase {
  static final int IP_MAX_ATTEMPTS = 5;

  static final int EMAIL_MAX_ATTEMPTS = 3;

  static final long IP_WINDOW_SIZE = ChronoUnit.MINUTES.getDuration().toMillis();

  static final long EMAIL_WINDOW_SIZE = ChronoUnit.HOURS.getDuration().toMillis();

  private final ConcurrentHashMap<String, RateLimitWindow> ipRateLimits = new ConcurrentHashMap<>();

  private final ConcurrentHashMap<String, RateLimitWindow> emailRateLimits = new ConcurrentHashMap<>();

  private final Clock clock;

  /**
   * Checks if a request from the given IP address and email is within the allowed rate limits. <p> It will update the rate limit counters
   * for both IP and email, and determine if the request is allowed. Logs when rate limits are exceeded. </p>
   *
   * @param ipAddress the IP address of the requestor
   * @param email the email address associated with the request
   * @return a {@link RateLimitResult} containing allowance status and remaining attempts info
   */
  @Override
  public RateLimitResult checkRateLimit(String ipAddress, String email) {
    var emailHash = hashEmail(email);
    var ipWindow = getOrCreateWindow(ipRateLimits, ipAddress, IP_MAX_ATTEMPTS, IP_WINDOW_SIZE);
    var emailWindow = getOrCreateWindow(emailRateLimits, emailHash, EMAIL_MAX_ATTEMPTS, EMAIL_WINDOW_SIZE);
    boolean ipAllowed = ipWindow.tryAddAttempt();
    boolean emailAllowed = emailWindow.tryAddAttempt();
    boolean allowed = ipAllowed && emailAllowed;

    if (!allowed) {
      log.info("Rate limit exceeded for IP: {} or email hash: {}", ipAddress, emailHash);
    }

    return new RateLimitResult(
        allowed,
        ipWindow.getRemainingAttempts(),
        ipWindow.getResetTime() / 1000,
        emailWindow.getRemainingAttempts(),
        emailWindow.getResetTime() / 1000);
  }

  /**
   * Retrieves an existing {@link RateLimitWindow} for the specified key from the map, or creates and stores a new one if it does not exist.
   *
   * @param map the map holding rate limit windows
   * @param key the key (IP address or email hash) for which to get or create the window
   * @param maxAttempts the maximum allowed attempts in the window
   * @param windowSizeMillis the size of the sliding window in milliseconds
   * @return the existing or newly created {@link RateLimitWindow}
   */
  private RateLimitWindow getOrCreateWindow(ConcurrentHashMap<String, RateLimitWindow> map,
      String key, int maxAttempts, long windowSizeMillis) {
    return map.compute(key, (k, window) -> window == null ? new RateLimitWindow(maxAttempts, windowSizeMillis, clock) : window);
  }

  /**
   * Returns a hash representation of the email address to avoid storing emails directly. <p> Uses lowercased email and computes hex string
   * of its hash code. </p>
   *
   * @param email the email address to hash
   * @return the hexadecimal string of the email's hash code
   */
  String hashEmail(String email) {
    return Integer.toHexString(email.toLowerCase().hashCode());
  }
}
