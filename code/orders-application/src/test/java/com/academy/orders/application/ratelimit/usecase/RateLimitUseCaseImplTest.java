package com.academy.orders.application.ratelimit.usecase;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RateLimitUseCaseImplTest {
  static final int IP_MAX_ATTEMPTS = RateLimitUseCaseImpl.IP_MAX_ATTEMPTS;

  static final int EMAIL_MAX_ATTEMPTS = RateLimitUseCaseImpl.EMAIL_MAX_ATTEMPTS;

  static final String BASE_EMAIL_DOMAIN = "@example.com";

  static final String FIXED_TIME_ISO = "2025-07-23T10:00:00Z";

  static final ZoneId ZONE_UTC = ZoneId.of("UTC");

  private Clock fixedClock;

  private RateLimitUseCaseImpl rateLimitUseCase;

  @BeforeEach
  void setUp() {
    fixedClock = Clock.fixed(Instant.parse(FIXED_TIME_ISO), ZONE_UTC);
    rateLimitUseCase = new RateLimitUseCaseImpl(fixedClock);
  }

  @Test
  void checkRateLimit_allowsUnderLimit_attemptsAreCounted() {
    // Given
    var ip = "192.168.1.1";
    for (var i = 0; i < IP_MAX_ATTEMPTS; i++) {
      var email = "user" + i + BASE_EMAIL_DOMAIN;
      // When
      var result = rateLimitUseCase.checkRateLimit(ip, email);
      // Then
      var expectedIpRemaining = IP_MAX_ATTEMPTS - (i + 1);
      var expectedEmailRemaining = EMAIL_MAX_ATTEMPTS - 1;

      assertTrue(result.isAllowed(), "Expected allowed for attempt " + i);
      assertEquals(expectedIpRemaining, result.remainingIpAttempts(), "Expected correct remaining IP attempts");
      assertEquals(expectedEmailRemaining, result.remainingEmailAttempts(), "Expected correct remaining Email attempts");
    }
  }

  @Test
  void checkRateLimit_blocksWhenIpLimitExceeded() {
    // Given
    var ip = "192.168.1.2";
    for (var i = 0; i < IP_MAX_ATTEMPTS; i++) {
      var email = "user" + i + BASE_EMAIL_DOMAIN;
      var result = rateLimitUseCase.checkRateLimit(ip, email);

      assertTrue(result.isAllowed(), "Expected allowed for attempt " + i);
      assertEquals(IP_MAX_ATTEMPTS - (i + 1), result.remainingIpAttempts(), "Expected correct remaining IP attempts");
      assertEquals(EMAIL_MAX_ATTEMPTS - 1, result.remainingEmailAttempts(),
          "Expected correct remaining email attempts after first attempt");
    }
    // When
    var blocked = rateLimitUseCase.checkRateLimit(ip, "user" + IP_MAX_ATTEMPTS + BASE_EMAIL_DOMAIN);
    // Then
    assertFalse(blocked.isAllowed(), "Expected blocked due to IP limit");
    assertEquals(0, blocked.remainingIpAttempts(), "Expected no remaining IP attempts");
  }

  @Test
  void checkRateLimit_blocksWhenEmailLimitExceeded() {
    // Given
    var ip1 = "10.0.0.1";
    var ip2 = "10.0.0.2";
    var email = "blocked" + BASE_EMAIL_DOMAIN;
    for (var i = 0; i < EMAIL_MAX_ATTEMPTS; i++) {
      var result = rateLimitUseCase.checkRateLimit(ip1, email);
      assertTrue(result.isAllowed(), "Expected allowed for attempt " + i);
    }
    // When
    var blocked = rateLimitUseCase.checkRateLimit(ip2, email);
    // Then
    assertFalse(blocked.isAllowed(), "Expected blocked due to email limit");
    assertEquals(0, blocked.remainingEmailAttempts());
  }

  @Test
  void checkRateLimit_allowsWhenUnderBothLimits() {
    // Given
    var ip = "123.45.67.89";
    var email = "test" + BASE_EMAIL_DOMAIN;
    // When & Then
    for (var i = 0; i < 2; i++) {
      var result = rateLimitUseCase.checkRateLimit(ip, email);
      assertTrue(result.isAllowed(), "Expected allowed attempt " + i);
    }
  }

  @Test
  void hashEmail_producesConsistentHex() {
    // Given
    var emailUpper = "USER" + BASE_EMAIL_DOMAIN.toUpperCase();
    var emailLower = "user" + BASE_EMAIL_DOMAIN;
    // When
    var hash1 = rateLimitUseCase.hashEmail(emailUpper);
    var hash2 = rateLimitUseCase.hashEmail(emailLower);
    // Then
    assertEquals(hash1, hash2, "Hash should be case-insensitive and consistent");
  }

  @Test
  void checkRateLimit_multipleAttemptsSameEmailReduceEmailRemaining() {
    // Given
    var ip = "8.8.8.8";
    var email = "multi" + BASE_EMAIL_DOMAIN;
    // When
    var first = rateLimitUseCase.checkRateLimit(ip, email);
    var second = rateLimitUseCase.checkRateLimit(ip, email);
    // Then
    assertTrue(first.isAllowed());
    assertTrue(second.isAllowed());
    assertEquals(EMAIL_MAX_ATTEMPTS - 1, first.remainingEmailAttempts());
    assertEquals(EMAIL_MAX_ATTEMPTS - 2, second.remainingEmailAttempts());
  }

  @Test
  void checkRateLimit_attemptsResetAfterWindow() {
    // Given
    var ip = "1.1.1.1";
    for (var i = 0; i < IP_MAX_ATTEMPTS; i++) {
      var result = rateLimitUseCase.checkRateLimit(ip, "reset" + i + BASE_EMAIL_DOMAIN);
      assertTrue(result.isAllowed());
    }
    // When
    var blocked = rateLimitUseCase.checkRateLimit(ip, "another" + BASE_EMAIL_DOMAIN);
    assertFalse(blocked.isAllowed());
    var advancedClock = Clock.fixed(fixedClock.instant().plusMillis(RateLimitUseCaseImpl.IP_WINDOW_SIZE + 1000), ZONE_UTC);
    var rateLimitUseCaseNew = new RateLimitUseCaseImpl(advancedClock);

    // Then
    var resultAfterReset = rateLimitUseCaseNew.checkRateLimit(ip, "new" + BASE_EMAIL_DOMAIN);
    assertTrue(resultAfterReset.isAllowed());
    assertEquals(IP_MAX_ATTEMPTS - 1, resultAfterReset.remainingIpAttempts());
  }

  @Test
  void checkRateLimit_returnsCorrectResetTime() {
    // Given
    var ip = "9.9.9.9";
    var email = "resettime" + BASE_EMAIL_DOMAIN;
    // When
    var result = rateLimitUseCase.checkRateLimit(ip, email);
    // Then
    var nowSeconds = fixedClock.millis() / 1000;
    assertTrue(result.ipResetTime() > nowSeconds);
    assertTrue(result.emailResetTime() > nowSeconds);
  }
}
