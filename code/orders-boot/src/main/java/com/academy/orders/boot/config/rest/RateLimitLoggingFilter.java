package com.academy.orders.boot.config.rest;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Spring filter that enforces rate limiting on the {@code /retail/v1/password-reset} endpoint using Resilience4j's {@link RateLimiter}. <p>
 * The filter limits requests based on client IP address, sets rate limit headers, and responds with HTTP 429 when the limit is exceeded. It
 * logs relevant request and response headers and periodically cleans up stale IP tracking data to avoid memory leaks. <p> The filter has
 * the highest precedence to ensure rate limiting is applied before other processing.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RateLimitLoggingFilter extends OncePerRequestFilter {

  private static final String PASSWORD_RESET_ENDPOINT = "/retail/v1/password-reset";

  private static final long CLEANUP_INTERVAL_MS = 3600000;

  private final RateLimiterRegistry rateLimiterRegistry;

  private final ConcurrentHashMap<String, Long> lastResetTimeByIp = new ConcurrentHashMap<>();

  private volatile long lastCleanupTime = System.currentTimeMillis();

  /**
   * Filters incoming HTTP requests, applying rate limiting for the password reset endpoint. If the rate limit is exceeded, responds with
   * 429 status and rate limit headers. Logs request and response headers for monitoring.
   *
   * @param request the incoming HTTP request
   * @param response the HTTP response
   * @param filterChain the filter chain to continue processing if not rate limited
   * @throws ServletException if a servlet error occurs
   * @throws IOException if an I/O error occurs while handling the request/response
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var uri = request.getRequestURI();
    log.info("Processing request for URI: {}", uri);

    if (uri.startsWith(PASSWORD_RESET_ENDPOINT) && isRateLimitExceeded(request, response)) {
      return;
    }

    filterChain.doFilter(request, response);

    if (uri.startsWith(PASSWORD_RESET_ENDPOINT)) {
      logResponseHeaders(response);
    }
  }

  /**
   * Checks if the request from the client IP exceeds the rate limit. If the limit is exceeded, sends a 429 response with appropriate
   * headers. Also updates internal tracking data and sets rate limit headers on successful requests.
   *
   * @param request HTTP request
   * @param response HTTP response
   * @return {@code true} if the request was rejected due to rate limiting; {@code false} otherwise
   * @throws IOException if writing the rejection response fails
   */
  private boolean isRateLimitExceeded(HttpServletRequest request, HttpServletResponse response) throws IOException {
    cleanupOldEntriesIfNeeded();

    var clientIp = normalizeIp(request.getRemoteAddr());
    var rateLimiter = rateLimiterRegistry.rateLimiter("passwordReset");
    logRateLimiterConfig(rateLimiter);

    long now = System.currentTimeMillis();
    var config = rateLimiter.getRateLimiterConfig();
    long refreshPeriodMillis = config.getLimitRefreshPeriod().toMillis();
    long currentWindowStart = now - (now % refreshPeriodMillis);

    long lastResetTime = lastResetTimeByIp.compute(clientIp, (k, v) -> (v == null || v < currentWindowStart) ? currentWindowStart : v);

    long resetInSeconds = calculateResetInSeconds(now, lastResetTime, refreshPeriodMillis);

    logRequestHeaders(request);

    boolean permitted = tryAcquirePermission(rateLimiter);
    int remaining = rateLimiter.getMetrics().getAvailablePermissions();

    if (!permitted) {
      rejectRequest(response, clientIp, request.getRequestURI(), resetInSeconds, remaining);
      return true;
    }
    setRateLimitHeaders(response, remaining, resetInSeconds);
    return false;
  }

  /**
   * Attempts to acquire permission from the rate limiter. Logs result and handles {@link RequestNotPermitted} exceptions by returning
   * {@code false}.
   *
   * @param rateLimiter the rate limiter instance
   * @return {@code true} if permission granted, {@code false} if rate limit exceeded
   */
  private boolean tryAcquirePermission(RateLimiter rateLimiter) {
    try {
      boolean acquired = rateLimiter.acquirePermission();
      log.info("Rate limit check: acquired={}, remaining={}", acquired, rateLimiter.getMetrics().getAvailablePermissions());
      return acquired;
    } catch (RequestNotPermitted e) {
      log.warn("Request rejected by rate limiter: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Periodically cleans up entries tracking client IP reset times that are older than twice the refresh period. This prevents unbounded
   * memory growth from tracking many unique client IPs.
   */
  private void cleanupOldEntriesIfNeeded() {
    long now = System.currentTimeMillis();
    if (now - lastCleanupTime < CLEANUP_INTERVAL_MS) {
      return;
    }
    var refreshPeriodMillis = rateLimiterRegistry.rateLimiter("passwordReset").getRateLimiterConfig().getLimitRefreshPeriod().toMillis();

    lastResetTimeByIp.entrySet().removeIf(entry -> now - entry.getValue() > refreshPeriodMillis * 2);
    lastCleanupTime = now;
    log.info("Cleaned up old IP entries from lastResetTimeByIp map");
  }

  /**
   * Sends a 429 Too Many Requests response with JSON error body and sets rate limit headers.
   *
   * @param response HTTP response to write to
   * @param clientIp IP address of the client
   * @param uri request URI
   * @param resetInSeconds seconds until the rate limit window resets
   * @param remaining number of remaining requests allowed in the current window
   * @throws IOException if writing the response body fails
   */
  private void rejectRequest(HttpServletResponse response, String clientIp, String uri, long resetInSeconds, int remaining)
      throws IOException {
    var timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now().atOffset(ZoneOffset.UTC));
    log.warn("Rate limit exceeded at {} for IP: {}, URI: {}", timestamp, clientIp, uri);

    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    setRateLimitHeaders(response, remaining, resetInSeconds);
    response.setContentType("application/json");
    response.getWriter().write("{\"error\": \"Rate limit exceeded. Please try again later.\"}");
  }

  /**
   * Sets custom rate limit headers on the response: <ul> <li>{@code X-RateLimit-Remaining-IP} - remaining requests allowed</li>
   * <li>{@code X-RateLimit-Reset-IP} - seconds until rate limit resets</li> </ul>
   *
   * @param response HTTP response to set headers on
   * @param remaining remaining requests count
   * @param resetInSeconds seconds until reset
   */
  private void setRateLimitHeaders(HttpServletResponse response, int remaining, long resetInSeconds) {
    response.setHeader("X-RateLimit-Remaining-IP", String.valueOf(remaining));
    response.setHeader("X-RateLimit-Reset-IP", String.valueOf(resetInSeconds));
    log.info("Set rate limit headers: X-RateLimit-Remaining-IP={}, X-RateLimit-Reset-IP={}", remaining, resetInSeconds);
  }

  /**
   * Logs all headers from the HTTP request.
   *
   * @param request HTTP request whose headers will be logged
   */
  private void logRequestHeaders(HttpServletRequest request) {
    log.info("Request headers for password reset:");
    Collections.list(request.getHeaderNames())
        .forEach(name -> log.info("Request header: {} = {}", name, request.getHeader(name)));
  }

  /**
   * Logs all headers from the HTTP response.
   *
   * @param response HTTP response whose headers will be logged
   */
  private void logResponseHeaders(HttpServletResponse response) {
    log.info("Matched password reset URI. Logging response headers.");
    response.getHeaderNames()
        .forEach(name -> log.info("Response header: {} = {}", name, response.getHeader(name)));
  }

  /**
   * Normalizes the client IP address, converting IPv6 localhost (::1) to IPv4 (127.0.0.1).
   *
   * @param ip raw IP address from the request
   * @return normalized IP address string
   */
  private String normalizeIp(String ip) {
    return ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) ? "127.0.0.1" : ip;
  }

  /**
   * Logs configuration details of the provided rate limiter for debugging purposes.
   *
   * @param rateLimiter the rate limiter instance
   */
  private void logRateLimiterConfig(RateLimiter rateLimiter) {
    var config = rateLimiter.getRateLimiterConfig();
    log.info("RateLimiter config: limitForPeriod={}, limitRefreshPeriod={}ms, timeoutDuration={}ms",
        config.getLimitForPeriod(), config.getLimitRefreshPeriod().toMillis(), config.getTimeoutDuration().toMillis());
  }

  /**
   * Calculates seconds remaining until the rate limit window resets.
   *
   * @param now current time in milliseconds
   * @param lastResetTime last reset time in milliseconds
   * @param refreshPeriodMillis refresh period duration in milliseconds
   * @return seconds until the next rate limit window reset (minimum zero)
   */
  private long calculateResetInSeconds(long now, long lastResetTime, long refreshPeriodMillis) {
    long resetTimestampMillis = lastResetTime + refreshPeriodMillis;
    return Math.max(0, (resetTimestampMillis - now) / 1000);
  }
}
