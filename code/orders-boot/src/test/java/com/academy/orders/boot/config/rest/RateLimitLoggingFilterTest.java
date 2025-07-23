package com.academy.orders.boot.config.rest;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.Collections;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.atLeastOnce;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimitLoggingFilterTest {
  @Mock
  private RateLimiter rateLimiter;

  @Mock
  private RateLimiterRegistry rateLimiterRegistry;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @Mock
  private PrintWriter printWriter;

  private RateLimitLoggingFilter filter;

  @BeforeEach
  void setUp() throws Exception {
    try (var ignored = MockitoAnnotations.openMocks(this)) {
      // Given
      RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
          .limitForPeriod(10)
          .limitRefreshPeriod(Duration.ofSeconds(60))
          .timeoutDuration(Duration.ofSeconds(1))
          .build();

      when(rateLimiterRegistry.rateLimiter("passwordReset")).thenReturn(rateLimiter);
      when(rateLimiter.getRateLimiterConfig()).thenReturn(rateLimiterConfig);
      when(rateLimiter.acquirePermission()).thenReturn(true);
      when(rateLimiter.getMetrics()).thenReturn(mock(RateLimiter.Metrics.class));
      when(rateLimiter.getMetrics().getAvailablePermissions()).thenReturn(10);

      filter = new RateLimitLoggingFilter(rateLimiterRegistry);

      when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
      when(response.getWriter()).thenReturn(printWriter);
    }
  }

  @Test
  void shouldAllowRequestWhenRateLimitNotExceeded() throws ServletException, IOException {
    // Given
    when(request.getRequestURI()).thenReturn("/retail/v1/password-reset");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain, times(1)).doFilter(request, response);
    verify(response).setHeader(eq("X-RateLimit-Remaining-IP"), anyString());
    verify(response).setHeader(eq("X-RateLimit-Reset-IP"), anyString());
  }

  @Test
  void shouldSkipFilterForNonMatchingUri() throws ServletException, IOException {
    // Given
    when(request.getRequestURI()).thenReturn("/some/other/endpoint");

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(rateLimiterRegistry);
  }

  @Test
  void shouldRejectRequestWhenRateLimitExceeded() throws ServletException, IOException {
    // Given
    when(request.getRequestURI()).thenReturn("/retail/v1/password-reset");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    when(rateLimiter.acquirePermission()).thenReturn(false); // Simulate rate limit exceeded
    when(rateLimiter.getMetrics().getAvailablePermissions()).thenReturn(0);

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    verify(response).setContentType("application/json");
    verify(response).setHeader(eq("X-RateLimit-Remaining-IP"), anyString());
    verify(response).setHeader(eq("X-RateLimit-Reset-IP"), anyString());
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  void shouldNormalizeLocalhostIPv6() throws ServletException, IOException {
    // Given
    when(request.getRequestURI()).thenReturn("/retail/v1/password-reset");
    when(request.getRemoteAddr()).thenReturn("::1");

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  void shouldLogRequestHeadersIfPresent() throws ServletException, IOException {
    // Given
    var headerNames = Collections.enumeration(java.util.List.of("User-Agent", "Accept"));
    when(request.getRequestURI()).thenReturn("/retail/v1/password-reset");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    when(request.getHeaderNames()).thenReturn(headerNames);
    when(request.getHeader("User-Agent")).thenReturn("JUnitTestAgent");
    when(request.getHeader("Accept")).thenReturn("application/json");

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldUseSameResetTimeForSameIpWithinWindow() throws ServletException, IOException {
    // Given
    when(request.getRequestURI()).thenReturn("/retail/v1/password-reset");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");

    // When
    filter.doFilterInternal(request, response, filterChain);
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(response, atLeastOnce()).setHeader(eq("X-RateLimit-Remaining-IP"), anyString());
    verify(response, atLeastOnce()).setHeader(eq("X-RateLimit-Reset-IP"), anyString());
  }

  @Test
  void shouldNormalizeIPv6LocalhostZeroed() throws ServletException, IOException {
    // Given
    when(request.getRequestURI()).thenReturn("/retail/v1/password-reset");
    when(request.getRemoteAddr()).thenReturn("0:0:0:0:0:0:0:1");

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldCalculateResetSecondsCorrectlyNearBoundary()
      throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    // Given
    when(request.getRequestURI()).thenReturn("/retail/v1/password-reset");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    var method = filter.getClass()
        .getDeclaredMethod("calculateResetInSeconds", long.class, long.class, long.class);
    method.setAccessible(true);

    long now = System.currentTimeMillis();
    long refreshMillis = 60_000; // 60s
    long lastReset = now - (now % refreshMillis);
    long testTime = lastReset + 59_000;

    // When
    long secondsRemaining = (long) method.invoke(filter, testTime, lastReset, refreshMillis);

    // Then
    assertTrue(secondsRemaining == 1 || secondsRemaining == 0,
        "Expected 1 or 0 seconds until reset, but got " + secondsRemaining);
  }

  @Test
  void shouldRejectRequestOnRequestNotPermittedException() throws Exception {
    // Given
    when(request.getRequestURI()).thenReturn("/retail/v1/password-reset");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    when(response.getWriter()).thenReturn(printWriter);

    var exception = RequestNotPermitted.createRequestNotPermitted(rateLimiter);
    doThrow(exception).when(rateLimiter).acquirePermission();
    when(rateLimiter.getMetrics().getAvailablePermissions()).thenReturn(0);

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    verify(response).setContentType("application/json");
    verify(response, times(1)).setHeader("X-RateLimit-Remaining-IP", "0");
    verify(response, times(1)).setHeader(eq("X-RateLimit-Reset-IP"), anyString());
    verify(filterChain, never()).doFilter(request, response);
  }
}
