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
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class PrimaryTokenFactoryTest {
  private PrimaryTokenFactory factory;

  private Clock fixedClock;

  @BeforeEach
  void setUp() {
    fixedClock = Clock.fixed(Instant.parse("2025-07-30T12:00:00Z"), ZoneOffset.UTC);
    factory = new PrimaryTokenFactory(fixedClock);
  }

  @Test
  void createToken_ShouldCreateValidPrimaryToken() {
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
    assertEquals(TokenType.PRIMARY, token.getType());
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
    assertNotEquals(token1.getToken(), token2.getToken(),
        "Tokens generated in different calls should be unique");
  }

  @Test
  void createToken_ShouldPreserveProvidedAccountIdAndEmail() {
    // Given
    Long accountId = 999L;
    String email = "another@example.com";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertEquals(accountId, token.getAccountId());
    assertEquals(email, token.getEmail());
  }

  @Test
  void createToken_ShouldSetTypeToPrimary() {
    // Given
    Long accountId = 1L;
    String email = "a@b.com";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertEquals(TokenType.PRIMARY, token.getType());
  }

  @Test
  void createToken_ShouldSetStatusToActive() {
    // Given
    Long accountId = 1L;
    String email = "a@b.com";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertEquals(TokenStatus.ACTIVE, token.getStatus());
  }

  @Test
  void createToken_ShouldSetExpiresAt10MinutesAfterCreatedAt() {
    // Given
    Long accountId = 1L;
    String email = "a@b.com";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    OffsetDateTime createdAt = token.getCreatedAt();
    OffsetDateTime expiresAt = token.getExpiresAt();

    assertEquals(createdAt.plusMinutes(10), expiresAt);
  }

  @Test
  void createToken_WithNullEmail_ShouldStillCreateToken() {
    // Given
    Long accountId = 1L;
    String email = null;

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertNotNull(token);
    assertEquals(accountId, token.getAccountId());
    assertNull(token.getEmail());
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

  @RepeatedTest(100)
  void createToken_ShouldGenerateUniqueTokens() {
    // Given
    Set<String> tokens = new HashSet<>();

    // When/Then
    for (int i = 0; i < 100; i++) {
      String t = factory.createToken(1L, "a@b.com").getToken();
      assertFalse(tokens.contains(t), "Duplicate token found: " + t);
      tokens.add(t);
    }
  }

  @Test
  void createToken_WithZeroAccountId() {
    // Given
    Long accountId = 0L;
    String email = "zero@example.com";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertEquals(accountId, token.getAccountId());
  }

  @Test
  void createToken_WithNegativeAccountId() {
    // Given
    Long accountId = -10L;
    String email = "neg@example.com";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertEquals(accountId, token.getAccountId());
  }

  @Test
  void createToken_WithEmptyEmail() {
    // Given
    Long accountId = 1L;
    String email = "";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertEquals(email, token.getEmail());
  }

  @Test
  void createToken_WithBlankEmail() {
    // Given
    Long accountId = 1L;
    String email = "   ";

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertEquals(email, token.getEmail());
  }

  @Test
  void createdAtAndExpiresAt_ShouldBe10MinutesApart() {
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
  void createToken_WithNullAccountIdAndEmail() {
    // Given
    Long accountId = null;
    String email = null;

    // When
    PasswordResetToken token = factory.createToken(accountId, email);

    // Then
    assertNotNull(token);
    assertNull(token.getAccountId());
    assertNull(token.getEmail());
    assertNotNull(token.getToken());
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
      java.util.UUID.fromString(token.getToken());
    });
  }

  @Test
  void createToken_WithDifferentClockZone() {
    // Given
    Clock clock = Clock.fixed(Instant.parse("2025-07-30T15:00:00Z"), ZoneOffset.ofHours(3));
    PrimaryTokenFactory factoryWithZone = new PrimaryTokenFactory(clock);
    Long accountId = 1L;
    String email = "zone@test.com";

    // When
    PasswordResetToken token = factoryWithZone.createToken(accountId, email);

    // Then
    assertEquals(OffsetDateTime.now(clock), token.getCreatedAt());
  }

  @Test
  void createToken_ShouldReturnDifferentObjects() {
    // Given
    Long accountId = 1L;
    String email1 = "test1@example.com";
    String email2 = "test2@example.com";

    // When
    PasswordResetToken token1 = factory.createToken(accountId, email1);
    PasswordResetToken token2 = factory.createToken(accountId, email2);

    // Then
    assertNotSame(token1, token2);
  }
}
