package com.academy.orders.infrastructure.account.factory;

import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenStatus;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class SecondaryTokenFactoryTest {
  private SecondaryTokenFactory factory;

  private Clock fixedClock;

  @BeforeEach
  void setUp() {
    fixedClock = Clock.fixed(Instant.parse("2025-07-30T12:00:00Z"), ZoneOffset.UTC);
    factory = new SecondaryTokenFactory(fixedClock);
  }

  @Test
  void createToken_ShouldCreateValidSecondaryToken() {
    // Given
    Long accountId = 123L;
    String email = "user@example.com";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertNotNull(token);
    assertNotNull(token.getToken());
    assertFalse(token.getToken().isBlank());
    assertEquals(accountId, token.getAccountId());
    assertEquals(email, token.getEmail());
    assertEquals(TokenType.SECONDARY, token.getType());
    assertEquals(TokenStatus.ACTIVE, token.getStatus());

    OffsetDateTime expectedCreatedAt = OffsetDateTime.now(fixedClock);
    OffsetDateTime expectedExpiresAt = expectedCreatedAt.plusMinutes(10);
    assertEquals(expectedCreatedAt, token.getCreatedAt());
    assertEquals(expectedExpiresAt, token.getExpiresAt());
  }

  @Test
  void createToken_ShouldGenerateUniqueTokensOnMultipleCalls() {
    // Given
    Long accountId = 123L;
    String email = "user@example.com";

    // When
    PasswordResetToken token1 = factory.createToken(accountId, email);
    PasswordResetToken token2 = factory.createToken(accountId, email);

    // Then
    assertNotEquals(token1.getToken(), token2.getToken(), "Tokens must be unique");
  }

  @Test
  void createToken_WithNullEmail_ShouldStillCreateToken() {
    // Given
    Long accountId = 123L;
    String email = null;

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertNotNull(token);
    assertEquals(accountId, token.getAccountId());
    assertNull(token.getEmail());
    assertEquals(TokenType.SECONDARY, token.getType());
  }

  @Test
  void createToken_WithNullAccountId_ShouldStillCreateToken() {
    // Given
    Long accountId = null;
    String email = "test@example.com";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertNotNull(token);
    assertNull(token.getAccountId());
    assertEquals(email, token.getEmail());
  }

  @RepeatedTest(50)
  void createToken_ShouldGenerateUniqueTokens() {
    // Given
    Set<String> tokens = new HashSet<>();

    // When/Then
    for (int i = 0; i < 50; i++) {
      String tokenStr = factory.createToken(1L, "test@example.com").getToken();

      // Then
      assertFalse(tokens.contains(tokenStr), "Duplicate token found: " + tokenStr);
      tokens.add(tokenStr);
    }
  }

  @Test
  void createToken_TokenIsValidUUID() {
    // Given
    Long accountId = 1L;
    String email = "uuid@test.com";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertDoesNotThrow(() -> {
      UUID.fromString(token.getToken());
    });
  }

  @Test
  void createToken_ExpiresAtIs10MinutesAfterCreatedAt() {
    // Given
    Long accountId = 1L;
    String email = "time@test.com";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    OffsetDateTime createdAt = token.getCreatedAt();
    OffsetDateTime expiresAt = token.getExpiresAt();
    assertEquals(createdAt.plusMinutes(10), expiresAt);
  }

  @Test
  void createToken_ShouldReturnDifferentObjects() {
    // Given
    Long accountId = 1L;
    String email1 = "user1@example.com";
    String email2 = "user2@example.com";

    // When
    PasswordResetToken token1 = factory.createToken(accountId, email1);
    PasswordResetToken token2 = factory.createToken(accountId, email2);

    // Then
    assertNotSame(token1, token2);
  }

  @Test
  void tokenShouldNeverBeEmptyOrBlank() {
    // Given
    Long accountId = 1L;
    String email = "test@example.com";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertNotNull(token.getToken());
    assertFalse(token.getToken().isBlank(), "Token should not be blank");
    assertFalse(token.getToken().isEmpty(), "Token should not be empty");
  }

  @Test
  void tokensUniqueInQuickSuccession() {
    // Given
    Set<String> generatedTokens = new HashSet<>();

    // When/Then
    for (int i = 0; i < 1000; i++) {
      String token = factory.createToken(1L, "test@example.com").getToken();

      // Then
      assertFalse(generatedTokens.contains(token), "Duplicate token detected: " + token);
      generatedTokens.add(token);
    }
  }

  @Test
  void timestampsConsistentWithClock() {
    // Given
    Long accountId = 1L;
    String email = "test@example.com";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);
    OffsetDateTime now = OffsetDateTime.now(fixedClock);

    // Then
    assertEquals(now, token.getCreatedAt(), "createdAt should match fixed clock time");
    assertEquals(now.plusMinutes(10), token.getExpiresAt(), "expiresAt should be 10 minutes after createdAt");
  }

  @Test
  void createTokenReturnsNewInstances() {
    // Given
    Long accountId = 1L;
    String email = "a@example.com";

    // When
    PasswordResetToken token1 = factory.createToken(accountId, email);
    PasswordResetToken token2 = factory.createToken(accountId, email);

    // Then
    assertNotSame(token1, token2, "Factory should return new instances, not cached");
  }

  @Test
  void createTokenWithLargeAccountId() {
    // Given
    long largeAccountId = Long.MAX_VALUE;
    String email = "largeid@example.com";

    // When
    PasswordResetToken token = factory.createToken(largeAccountId, email);

    // Then
    assertEquals(largeAccountId, token.getAccountId());
  }

  @Test
  void createTokenWithSpecialCharactersInEmail() {
    // Given
    String specialEmail = "user+filter@example-domain.com";
    Long accountId = 1L;

    // When
    PasswordResetToken token = factory.createToken(accountId, specialEmail);

    // Then
    assertEquals(specialEmail, token.getEmail());
  }

  @Test
  void createTokenWithEmptyEmail() {
    // Given
    Long accountId = 1L;
    String email = "";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertEquals("", token.getEmail());
  }

  @Test
  void createTokenWithWhitespaceEmail() {
    // Given
    String whitespaceEmail = "   ";
    Long accountId = 1L;

    // When
    PasswordResetToken token = factory.createToken(accountId, whitespaceEmail);

    // Then
    assertEquals(whitespaceEmail, token.getEmail());
  }

  @Test
  void createTokenMultipleCallsWithNulls() {
    // Given/When/Then repeated
    for (int i = 0; i < 10; i++) {
      PasswordResetToken token = factory.createToken(null, null);

      assertNotNull(token.getToken());
      assertNull(token.getAccountId());
      assertNull(token.getEmail());
    }
  }

  @Test
  void tokenIsUUIDFormat() {
    // Given
    Long accountId = 1L;
    String email = "uuid@test.com";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertDoesNotThrow(() -> UUID.fromString(token.getToken()));
  }

  @Test
  void loggingCheck() {
    // Given
    Long accountId = 1L;
    String email = "logcheck@example.com";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertNotNull(token);
  }
}
