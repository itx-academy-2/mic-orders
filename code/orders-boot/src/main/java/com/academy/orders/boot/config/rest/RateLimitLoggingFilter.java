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
 * A Spring filter that enforces rate limiting for the {@code /retail/v1/password-reset} endpoint using Resilience4j's {@link RateLimiter}.
 * It logs request and response headers, limits requests based on client IP, and returns a {@code 429 Too Many Requests} response when the
 * limit is exceeded. The filter sets custom headers: <ul> <li><strong>X-RateLimit-Remaining-IP</strong>: Number of requests remaining in
 * the current rate limit window.</li> <li><strong>X-RateLimit-Reset-IP</strong>: Seconds until the rate limit window resets.</li> </ul>
 * These headers are non-standard but follow common API conventions for rate limiting. Clients should parse them to monitor rate limit
 * status. To avoid "unknown header" warnings in tools like Swagger UI, these headers are documented in the OpenAPI specification.
 *
 * <p>This filter has the highest precedence to ensure rate limiting is applied before other filters or controllers process the request.</p>
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RateLimitLoggingFilter extends OncePerRequestFilter {
  private static final String PASSWORD_RESET_ENDPOINT = "/retail/v1/password-reset";

  private final RateLimiterRegistry rateLimiterRegistry;

  private final ConcurrentHashMap<String, Long> lastResetTimeByIp = new ConcurrentHashMap<>();

  /**
   * Filters incoming HTTP requests, applying rate limiting to the password reset endpoint. If the rate limit is exceeded, responds with a
   * {@code 429 Too Many Requests} status and appropriate headers. Logs request and response headers for debugging.
   *
   * @param request the incoming HTTP request
   * @param response the HTTP response
   * @param filterChain the filter chain to continue processing if rate limit is not exceeded
   * @throws ServletException if a servlet error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var uri = request.getRequestURI();
    log.info("Processing request for URI: {}", uri);

    if (uri.startsWith(PASSWORD_RESET_ENDPOINT) && applyRateLimit(request, response)) {
      return;
    }

    filterChain.doFilter(request, response);

    if (uri.startsWith(PASSWORD_RESET_ENDPOINT)) {
      logResponseHeaders(response);
    }
  }

  /**
   * Applies rate limiting for the password reset endpoint. Checks the rate limit for the client's IP, sets headers, and rejects requests if
   * the limit is exceeded.
   *
   * @param request the incoming HTTP request
   * @param response the HTTP response
   * @return true if the request was rejected (response committed), false if allowed
   * @throws IOException if an I/O error occurs while writing the response
   */
  private boolean applyRateLimit(HttpServletRequest request, HttpServletResponse response) throws IOException {
    var clientIp = normalizeIp(request.getRemoteAddr());
    logRequestHeaders(request);

    var rateLimiter = rateLimiterRegistry.rateLimiter("passwordReset");
    logRateLimiterConfig(rateLimiter);

    var now = System.currentTimeMillis();
    var config = rateLimiter.getRateLimiterConfig();
    var refreshPeriodMillis = config.getLimitRefreshPeriod().toMillis();
    var lastResetTime = lastResetTimeByIp.getOrDefault(clientIp, now - (now % refreshPeriodMillis));
    var resetInSeconds = calculateResetInSeconds(now, lastResetTime, refreshPeriodMillis);

    try {
      var acquired = rateLimiter.acquirePermission();
      var remaining = rateLimiter.getMetrics().getAvailablePermissions();
      log.info("Rate limit check: acquired={}, remaining={}, clientIp={}", acquired, remaining, clientIp);

      if (!acquired) {
        rejectRequest(response, clientIp, request.getRequestURI(), resetInSeconds, remaining);
        return true;
      }

      lastResetTimeByIp.put(clientIp, now - (now % refreshPeriodMillis));
      setRateLimitHeaders(response, remaining, resetInSeconds);
      return false;
    } catch (RequestNotPermitted e) {
      var remaining = rateLimiter.getMetrics().getAvailablePermissions();
      rejectRequest(response, clientIp, request.getRequestURI(), resetInSeconds, remaining);
      return true;
    }
  }

  /**
   * Rejects a request when the rate limit is exceeded, setting a {@code 429 Too Many Requests} status and rate limit headers.
   *
   * @param response the HTTP response
   * @param clientIp the client's IP address
   * @param uri the request URI
   * @param resetInSeconds the time until the rate limit resets (in seconds)
   * @param remaining the number of remaining requests allowed
   * @throws IOException if an I/O error occurs while writing the response
   */
  private void rejectRequest(HttpServletResponse response, String clientIp, String uri,
      long resetInSeconds, int remaining) throws IOException {
    var timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now().atOffset(ZoneOffset.UTC));
    log.warn("Rate limit exceeded at {} for IP: {}, URI: {}", timestamp, clientIp, uri);

    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    setRateLimitHeaders(response, remaining, resetInSeconds);
    response.setContentType("application/json");
    response.getWriter().write("{\"error\": \"Rate limit exceeded. Please try again later.\"}");
  }

  /**
   * Sets rate limit response headers: {@code X-RateLimit-Remaining-IP} for remaining requests and {@code X-RateLimit-Reset-IP} for the time
   * until the rate limit resets.
   *
   * @param response the HTTP response
   * @param remaining the number of remaining requests allowed
   * @param resetInSeconds the time until the rate limit resets (in seconds)
   */
  private void setRateLimitHeaders(HttpServletResponse response, int remaining, long resetInSeconds) {
    response.setHeader("X-RateLimit-Remaining-IP", String.valueOf(remaining));
    response.setHeader("X-RateLimit-Reset-IP", String.valueOf(resetInSeconds));
    log.info("Set rate limit headers: X-RateLimit-Remaining-IP={}, X-RateLimit-Reset-IP={}", remaining, resetInSeconds);
  }

  /**
   * Logs all request headers for the password reset endpoint.
   *
   * @param request the incoming HTTP request
   */
  private void logRequestHeaders(HttpServletRequest request) {
    log.info("Request headers for password reset:");
    Collections.list(request.getHeaderNames())
        .forEach(headerName -> log.info("Request header: {} = {}", headerName, request.getHeader(headerName)));
  }

  /**
   * Logs all response headers for the password reset endpoint.
   *
   * @param response the HTTP response
   */
  private void logResponseHeaders(HttpServletResponse response) {
    log.info("Matched password reset URI. Logging response headers.");
    response.getHeaderNames()
        .forEach(headerName -> log.info("Response header: {} = {}", headerName, response.getHeader(headerName)));
  }

  /**
   * Normalizes the client IP address, converting localhost IPv6 addresses to IPv4.
   *
   * @param ip the raw IP address from the request
   * @return the normalized IP address (e.g., "127.0.0.1" for localhost)
   */
  private String normalizeIp(String ip) {
    return ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) ? "127.0.0.1" : ip;
  }

  /**
   * Logs the configuration of the rate limiter for debugging purposes.
   *
   * @param rateLimiter the Resilience4j RateLimiter instance
   */
  private void logRateLimiterConfig(RateLimiter rateLimiter) {
    var config = rateLimiter.getRateLimiterConfig();
    log.info("RateLimiter config: limitForPeriod={}, limitRefreshPeriod={}ms, timeoutDuration={}ms",
        config.getLimitForPeriod(), config.getLimitRefreshPeriod().toMillis(), config.getTimeoutDuration().toMillis());
  }

  /**
   * Calculates the time until the rate limit resets, in seconds.
   *
   * @param now the current time in milliseconds
   * @param lastResetTime the last reset time in milliseconds
   * @param refreshPeriodMillis the rate limit refresh period in milliseconds
   * @return the time until the next reset in seconds
   */
  private long calculateResetInSeconds(long now, long lastResetTime, long refreshPeriodMillis) {
    var resetTimestampMillis = lastResetTime + refreshPeriodMillis;
    return Math.max(0, (resetTimestampMillis - now) / 1000);
  }
}
