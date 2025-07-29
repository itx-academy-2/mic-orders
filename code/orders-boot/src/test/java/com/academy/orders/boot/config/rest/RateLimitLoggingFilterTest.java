package com.academy.orders.boot.config.rest;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.Collections;

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

  private ListAppender<ILoggingEvent> logAppender;

  static final String ENDPOINT = "/retail/v1/password-reset";

  static final String IPV6_LOOPBACK_1 = "::1";

  static final String IPV6_LOOPBACK_2 = "0:0:0:0:0:0:0:1";

  static final String IPV4_LOOPBACK = "127.0.0.1";

  static final String IPV4_CUSTOM = "192.168.1.1";

  static final long THIRTY_MINUTES_MS = 30 * 60_000L;

  static final long CLEANUP_INTERVAL_MS = 3_600_000L;

  static final long REFRESH_PERIOD_MILLIS = 60_000L;

  static final long OFFSET_MILLIS = 10_000L;

  static final long EXPECTED_SECONDS_REMAINING = 50L;

  @BeforeEach
  void setUp() throws Exception {
    try (var ignored = MockitoAnnotations.openMocks(this)) {
      // Given
      var rateLimiterConfig = RateLimiterConfig.custom()
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

      Logger logger = (Logger) LoggerFactory.getLogger(RateLimitLoggingFilter.class);
      logAppender = new ListAppender<>();
      logAppender.start();
      logger.addAppender(logAppender);
    }
  }

  @Test
  void shouldAllowRequestWhenRateLimitNotExceeded() throws ServletException, IOException {
    // Given
    when(request.getRequestURI()).thenReturn(ENDPOINT);
    when(request.getRemoteAddr()).thenReturn(IPV4_LOOPBACK);

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
    when(request.getRequestURI()).thenReturn(ENDPOINT);
    when(request.getRemoteAddr()).thenReturn(IPV4_LOOPBACK);
    when(rateLimiter.acquirePermission()).thenReturn(false);
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
    when(request.getRequestURI()).thenReturn(ENDPOINT);
    when(request.getRemoteAddr()).thenReturn("::1");

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  void shouldUseSameResetTimeForSameIpWithinWindow() throws ServletException, IOException {
    // Given
    when(request.getRequestURI()).thenReturn(ENDPOINT);
    when(request.getRemoteAddr()).thenReturn(IPV4_LOOPBACK);

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
    when(request.getRequestURI()).thenReturn(ENDPOINT);
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
    when(request.getRequestURI()).thenReturn(ENDPOINT);
    when(request.getRemoteAddr()).thenReturn(IPV4_LOOPBACK);
    var method = filter.getClass()
        .getDeclaredMethod("calculateResetInSeconds", long.class, long.class, long.class);
    method.setAccessible(true);

    long now = System.currentTimeMillis();
    long refreshMillis = 60_000;
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
    when(request.getRequestURI()).thenReturn(ENDPOINT);
    when(request.getRemoteAddr()).thenReturn(IPV4_LOOPBACK);
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

  @Test
  void shouldCalculateCurrentWindowStartCorrectly() throws Exception {
    // Given
    when(request.getRequestURI()).thenReturn(ENDPOINT);
    when(request.getRemoteAddr()).thenReturn(IPV4_LOOPBACK);
    var method = filter.getClass().getDeclaredMethod("isRateLimitExceeded", HttpServletRequest.class, HttpServletResponse.class);
    method.setAccessible(true);

    long now = System.currentTimeMillis();
    long refreshPeriodMillis = 60_000;
    var config = RateLimiterConfig.custom()
        .limitForPeriod(10)
        .limitRefreshPeriod(Duration.ofMillis(refreshPeriodMillis))
        .timeoutDuration(Duration.ofSeconds(1))
        .build();
    when(rateLimiter.getRateLimiterConfig()).thenReturn(config);

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    long expectedWindowStart = now - (now % refreshPeriodMillis);
    var lastResetTimeByIpField = filter.getClass().getDeclaredField("lastResetTimeByIp");
    lastResetTimeByIpField.setAccessible(true);
    ConcurrentHashMap<String, Long> lastResetTimeByIp = (ConcurrentHashMap<String, Long>) lastResetTimeByIpField.get(filter);
    assertEquals(expectedWindowStart, lastResetTimeByIp.get(IPV4_LOOPBACK));
  }

  @Test
  void shouldIncludeErrorMessageInResponseBodyWhenRateLimitExceeded() throws ServletException, IOException {
    // Given
    when(request.getRequestURI()).thenReturn(ENDPOINT);
    when(request.getRemoteAddr()).thenReturn(IPV4_LOOPBACK);
    when(rateLimiter.acquirePermission()).thenReturn(false);
    when(rateLimiter.getMetrics().getAvailablePermissions()).thenReturn(0);

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    verify(printWriter).write("{\"error\": \"Rate limit exceeded. Please try again later.\"}");
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  void shouldNormalizeIpCorrectly() throws Exception {
    // Given
    var normalizeIp = filter.getClass().getDeclaredMethod("normalizeIp", String.class);
    normalizeIp.setAccessible(true);

    // When & Then
    assertEquals(IPV4_LOOPBACK, normalizeIp.invoke(filter, IPV6_LOOPBACK_1));
    assertEquals(IPV4_LOOPBACK, normalizeIp.invoke(filter, IPV6_LOOPBACK_2));
    assertEquals(IPV4_CUSTOM, normalizeIp.invoke(filter, IPV4_CUSTOM));
  }

  @Test
  void shouldCalculateResetSecondsCorrectly() throws Exception {
    // Given
    var method = filter.getClass().getDeclaredMethod("calculateResetInSeconds", long.class, long.class, long.class);
    method.setAccessible(true);
    long now = System.currentTimeMillis();
    long lastReset = now - (now % REFRESH_PERIOD_MILLIS);
    long testTime = lastReset + OFFSET_MILLIS;

    // When
    long secondsRemaining = (long) method.invoke(filter, testTime, lastReset, REFRESH_PERIOD_MILLIS);

    // Then
    assertEquals(EXPECTED_SECONDS_REMAINING, secondsRemaining);
  }

  @Test
  void shouldCleanupOldEntries() throws Exception {
    // Given
    when(request.getRequestURI()).thenReturn(ENDPOINT);
    when(request.getRemoteAddr()).thenReturn(IPV4_LOOPBACK);

    var lastResetTimeByIpField = filter.getClass().getDeclaredField("lastResetTimeByIp");
    lastResetTimeByIpField.setAccessible(true);
    ConcurrentHashMap<String, Long> lastResetTimeByIp = (ConcurrentHashMap<String, Long>) lastResetTimeByIpField.get(filter);

    long oldTime = System.currentTimeMillis() - THIRTY_MINUTES_MS - 1;
    lastResetTimeByIp.put(IPV4_CUSTOM, oldTime);

    var lastCleanupTimeField = filter.getClass().getDeclaredField("lastCleanupTime");
    lastCleanupTimeField.setAccessible(true);

    lastCleanupTimeField.set(filter, System.currentTimeMillis() - CLEANUP_INTERVAL_MS - 1);
    assertTrue(lastResetTimeByIp.containsKey(IPV4_CUSTOM));

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    assertFalse(lastResetTimeByIp.containsKey(IPV4_CUSTOM), "Old IP should be cleaned up");
  }

  @Test
  void shouldNotCleanupIfIntervalNotPassed() throws Exception {
    // Given
    var lastCleanupTimeField = filter.getClass().getDeclaredField("lastCleanupTime");
    lastCleanupTimeField.setAccessible(true);
    long now = System.currentTimeMillis();
    lastCleanupTimeField.set(filter, now);

    var lastResetTimeByIpField = filter.getClass().getDeclaredField("lastResetTimeByIp");
    lastResetTimeByIpField.setAccessible(true);
    ConcurrentHashMap<String, Long> lastResetTimeByIp = (ConcurrentHashMap<String, Long>) lastResetTimeByIpField.get(filter);

    // When
    var oldIp = "10.0.0.1";
    lastResetTimeByIp.put(oldIp, now - 10 * 3600_000L);

    when(request.getRequestURI()).thenReturn(ENDPOINT);
    when(request.getRemoteAddr()).thenReturn(IPV4_LOOPBACK);

    // Then
    filter.doFilterInternal(request, response, filterChain);
    assertTrue(lastResetTimeByIp.containsKey(oldIp));
  }

  @Test
  void shouldKeepLastResetTimeForCurrentWindow() throws Exception {
    // Given
    when(request.getRequestURI()).thenReturn(ENDPOINT);
    when(request.getRemoteAddr()).thenReturn(IPV4_LOOPBACK);
    var lastResetTimeByIpField = filter.getClass().getDeclaredField("lastResetTimeByIp");
    lastResetTimeByIpField.setAccessible(true);
    ConcurrentHashMap<String, Long> lastResetTimeByIp = (ConcurrentHashMap<String, Long>) lastResetTimeByIpField.get(filter);
    long now = System.currentTimeMillis();
    long currentWindowStart = now - (now % REFRESH_PERIOD_MILLIS);
    lastResetTimeByIp.put(IPV4_LOOPBACK, currentWindowStart);

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    assertEquals(currentWindowStart, lastResetTimeByIp.get(IPV4_LOOPBACK));
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldUpdateLastResetTimeForNewIp() throws Exception {
    // Given
    when(request.getRequestURI()).thenReturn(ENDPOINT);
    when(request.getRemoteAddr()).thenReturn(IPV4_CUSTOM);
    long now = System.currentTimeMillis();
    long expectedWindowStart = now - (now % REFRESH_PERIOD_MILLIS);

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    var lastResetTimeByIpField = filter.getClass().getDeclaredField("lastResetTimeByIp");
    lastResetTimeByIpField.setAccessible(true);
    ConcurrentHashMap<String, Long> lastResetTimeByIp = (ConcurrentHashMap<String, Long>) lastResetTimeByIpField.get(filter);
    assertEquals(expectedWindowStart, lastResetTimeByIp.get(IPV4_CUSTOM));
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldLogResponseHeadersForPasswordReset() throws ServletException, IOException {
    // Given
    when(request.getRequestURI()).thenReturn(ENDPOINT);
    when(request.getRemoteAddr()).thenReturn(IPV4_LOOPBACK);
    when(response.getHeaderNames()).thenReturn(List.of("X-RateLimit-Remaining-IP", "X-RateLimit-Reset-IP"));
    when(response.getHeader("X-RateLimit-Remaining-IP")).thenReturn("10");
    when(response.getHeader("X-RateLimit-Reset-IP")).thenReturn("60");

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    assertTrue(logAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains("Matched password reset URI. Logging response headers.")));
    assertTrue(logAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains("Response header: X-RateLimit-Remaining-IP = 10")));
    assertTrue(logAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains("Response header: X-RateLimit-Reset-IP = 60")));

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldLogRequestHeadersForPasswordReset() throws ServletException, IOException {
    // Given
    when(request.getRequestURI()).thenReturn(ENDPOINT);
    when(request.getRemoteAddr()).thenReturn(IPV4_LOOPBACK);
    var headerNames = Collections.enumeration(List.of("User-Agent", "Accept"));
    when(request.getHeaderNames()).thenReturn(headerNames);
    when(request.getHeader("User-Agent")).thenReturn("JUnitTestAgent");
    when(request.getHeader("Accept")).thenReturn("application/json");

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    assertTrue(logAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains("Request headers for password reset:")));
    assertTrue(logAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains("Request header: User-Agent = JUnitTestAgent")));
    assertTrue(logAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains("Request header: Accept = application/json")));

    verify(filterChain).doFilter(request, response);
  }
}
