package com.academy.orders.application.passwordreset;

import static com.academy.orders.application.ModelUtils.createPrimaryToken;
import static com.academy.orders.application.ModelUtils.createSecondaryToken;
import com.academy.orders.application.passwordreset.usecase.ValidateTokenUseCaseImpl;
import com.academy.orders.domain.account.factory.PasswordResetTokenFactoryUseCase;
import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenStatus;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import com.academy.orders.domain.passwordreset.exception.InvalidTokenException;
import com.academy.orders.domain.passwordreset.exception.TokenNotFoundException;
import com.academy.orders.domain.passwordreset.repository.PasswordResetTokenRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidateTokenUseCaseImplTest {
  @Mock
  private PasswordResetTokenRepository tokenRepository;

  @Mock
  private PasswordResetTokenFactoryUseCase tokenFactory;

  @InjectMocks
  private ValidateTokenUseCaseImpl validateTokenUseCase;

  private static final String PRIMARY_TOKEN = "550e8400-e29b-41d4-a716-446655440000";

  private static final Long ACCOUNT_ID = 123L;

  private static final String EMAIL = "test@example.com";

  private static final Instant NOW_INSTANT = Instant.now();

  private final Clock clock = Clock.fixed(NOW_INSTANT, ZoneOffset.UTC);

  private PasswordResetToken primaryToken;

  @BeforeEach
  void setup() {
    primaryToken = createPrimaryToken(PRIMARY_TOKEN, EMAIL, ACCOUNT_ID, NOW_INSTANT, 600);
    validateTokenUseCase = new ValidateTokenUseCaseImpl(tokenRepository, tokenFactory, clock);
  }

  @Test
  void validatePrimaryToken_whenValidToken_shouldCreateAndReturnSecondaryToken() {
    // Given
    when(tokenRepository.findByToken(PRIMARY_TOKEN)).thenReturn(Optional.of(primaryToken));

    var secondaryTokenUuid = "b3c1f1b3-477e-45c2-9f3c-cccfbb8cd44e";
    var secondaryToken = createSecondaryToken(
        secondaryTokenUuid,
        EMAIL,
        ACCOUNT_ID,
        NOW_INSTANT,
        600);

    when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(tokenFactory.createToken(ACCOUNT_ID, EMAIL)).thenReturn(secondaryToken);

    // When
    var result = validateTokenUseCase.validatePrimaryToken(PRIMARY_TOKEN);

    // Then
    assertNotNull(result, "Validation result should not be null");
    assertTrue(result.valid(), "Token should be valid");
    assertEquals(UUID.fromString(PRIMARY_TOKEN), result.token(), "Returned token UUID must match primary token");
    assertEquals("password_reset", result.tokenType(), "Token type should be 'password_reset'");
    assertEquals(secondaryToken.getExpiresAt().toInstant(), result.expiresAt(), "Expiration date must match secondary token");
    assertEquals(UUID.fromString(secondaryTokenUuid), result.newToken(), "New token UUID must match secondary token");

    verify(tokenRepository).save(argThat(token -> token.getToken().equals(primaryToken.getToken())
        &&
        token.getStatus() == TokenStatus.USED));
    verify(tokenRepository).save(argThat(token -> token.getToken().equals(secondaryToken.getToken())
        &&
        token.getStatus() == TokenStatus.ACTIVE &&
        token.getType() == TokenType.SECONDARY));
    verify(tokenRepository).findByToken(PRIMARY_TOKEN);
    verify(tokenFactory).createToken(ACCOUNT_ID, EMAIL);
  }

  @Test
  void validatePrimaryToken_whenTokenNotFound_shouldThrowTokenNotFoundException() {
    // Given
    when(tokenRepository.findByToken(PRIMARY_TOKEN)).thenReturn(Optional.empty());

    // When & Then
    var ex = assertThrows(TokenNotFoundException.class,
        () -> validateTokenUseCase.validatePrimaryToken(PRIMARY_TOKEN),
        "Expected TokenNotFoundException when token not found");

    assertTrue(ex.getMessage().contains(PRIMARY_TOKEN), "Exception message should contain the token value");
  }

  @Test
  void validatePrimaryToken_whenTokenAlreadyUsed_shouldThrowInvalidTokenException() {
    // Given
    var usedToken = primaryToken.toBuilder()
        .status(TokenStatus.USED)
        .build();
    when(tokenRepository.findByToken(PRIMARY_TOKEN)).thenReturn(Optional.of(usedToken));

    // When & Then
    var ex = assertThrows(InvalidTokenException.class,
        () -> validateTokenUseCase.validatePrimaryToken(PRIMARY_TOKEN),
        "Expected InvalidTokenException when token is already used");

    assertEquals("Token already used.", ex.getMessage(), "Exception message mismatch");
  }

  @Test
  void validatePrimaryToken_whenTokenExpired_shouldThrowInvalidTokenException() {
    // Given
    var expiredToken = primaryToken.toBuilder()
        .expiresAt(OffsetDateTime.ofInstant(NOW_INSTANT, ZoneOffset.UTC).minusMinutes(1))
        .build();
    when(tokenRepository.findByToken(PRIMARY_TOKEN)).thenReturn(Optional.of(expiredToken));

    // When & Then
    var ex = assertThrows(InvalidTokenException.class,
        () -> validateTokenUseCase.validatePrimaryToken(PRIMARY_TOKEN),
        "Expected InvalidTokenException when token is expired");

    assertEquals("Token expired.", ex.getMessage(), "Exception message mismatch");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  void validatePrimaryToken_invalidTokens_throwsInvalidTokenException(String invalidToken) {
    InvalidTokenException ex = assertThrows(InvalidTokenException.class,
        () -> validateTokenUseCase.validatePrimaryToken(invalidToken));

    assertEquals("Token must not be null or empty.", ex.getMessage());
  }

  @Test
  void validatePrimaryToken_tokenFactoryThrowsException_propagatesException() {
    // Given
    when(tokenRepository.findByToken(PRIMARY_TOKEN)).thenReturn(Optional.of(primaryToken));
    when(tokenFactory.createToken(ACCOUNT_ID, EMAIL)).thenThrow(new RuntimeException("Factory failure"));

    // When & Then
    var ex = assertThrows(RuntimeException.class,
        () -> validateTokenUseCase.validatePrimaryToken(PRIMARY_TOKEN));

    assertEquals("Factory failure", ex.getMessage());
  }

  @Test
  void validatePrimaryToken_tokenRepositorySaveThrowsException_propagatesException() {
    // Given
    when(tokenRepository.findByToken(PRIMARY_TOKEN)).thenReturn(Optional.of(primaryToken));

    var secondaryTokenUuid = "b3c1f1b3-477e-45c2-9f3c-cccfbb8cd44e";
    var secondaryToken = createSecondaryToken(
        secondaryTokenUuid, EMAIL, ACCOUNT_ID, NOW_INSTANT, 600);

    when(tokenFactory.createToken(ACCOUNT_ID, EMAIL)).thenReturn(secondaryToken);
    when(tokenRepository.save(argThat(token -> token != null && secondaryToken.getToken().equals(token.getToken()))))
        .thenThrow(new RuntimeException("DB save error"));
    when(tokenRepository.save(argThat(token -> token != null && !secondaryToken.getToken().equals(token.getToken()))))
        .thenReturn(primaryToken.toBuilder().status(TokenStatus.USED).build());

    // When & Then
    var ex = assertThrows(RuntimeException.class,
        () -> validateTokenUseCase.validatePrimaryToken(PRIMARY_TOKEN));

    assertEquals("DB save error", ex.getMessage());
  }

  @Test
  void validatePrimaryToken_tokenStatusNull_throwsInvalidTokenException() {
    // Given
    var tokenWithNullStatus = primaryToken.toBuilder()
        .status(null)
        .build();
    when(tokenRepository.findByToken(PRIMARY_TOKEN)).thenReturn(Optional.of(tokenWithNullStatus));

    // When & Then
    var ex = assertThrows(InvalidTokenException.class,
        () -> validateTokenUseCase.validatePrimaryToken(PRIMARY_TOKEN));

    assertEquals("Token status is invalid.", ex.getMessage());
  }

  @Test
  void validatePrimaryToken_tokenTypeNotPrimary_throwsInvalidTokenException() {
    // Given
    var nonPrimaryToken = primaryToken.toBuilder()
        .type(TokenType.SECONDARY)
        .build();
    when(tokenRepository.findByToken(PRIMARY_TOKEN)).thenReturn(Optional.of(nonPrimaryToken));

    // When & Then
    var ex = assertThrows(InvalidTokenException.class,
        () -> validateTokenUseCase.validatePrimaryToken(PRIMARY_TOKEN));

    assertEquals("Token type is invalid.", ex.getMessage());
  }

  @Test
  void validatePrimaryToken_tokenExpiredJustBeforeNow_throwsInvalidTokenException() {
    // Given
    var tokenExpiredJustBeforeNow = primaryToken.toBuilder()
        .expiresAt(OffsetDateTime.ofInstant(NOW_INSTANT.minusMillis(1), ZoneOffset.UTC))
        .build();
    when(tokenRepository.findByToken(PRIMARY_TOKEN)).thenReturn(Optional.of(tokenExpiredJustBeforeNow));

    // When & Then
    var ex = assertThrows(InvalidTokenException.class,
        () -> validateTokenUseCase.validatePrimaryToken(PRIMARY_TOKEN));

    assertEquals("Token expired.", ex.getMessage());
  }

  @Test
  void validatePrimaryToken_tokenExpiresAtExactlyNow_doesNotThrow() {
    // Given
    var tokenExpiresAtNow = primaryToken.toBuilder()
        .expiresAt(OffsetDateTime.ofInstant(NOW_INSTANT, ZoneOffset.UTC))
        .build();
    when(tokenRepository.findByToken(PRIMARY_TOKEN)).thenReturn(Optional.of(tokenExpiresAtNow));

    var usedPrimaryToken = tokenExpiresAtNow.toBuilder()
        .status(TokenStatus.USED)
        .build();
    when(tokenRepository.save(usedPrimaryToken)).thenReturn(usedPrimaryToken);

    var secondaryToken = createSecondaryToken(
        "b3c1f1b3-477e-45c2-9f3c-cccfbb8cd44e",
        EMAIL,
        ACCOUNT_ID,
        NOW_INSTANT,
        600);
    when(tokenFactory.createToken(ACCOUNT_ID, EMAIL)).thenReturn(secondaryToken);
    when(tokenRepository.save(secondaryToken)).thenReturn(secondaryToken);

    // When & Then
    assertDoesNotThrow(() -> validateTokenUseCase.validatePrimaryToken(PRIMARY_TOKEN));

    verify(tokenFactory).createToken(ACCOUNT_ID, EMAIL);
    verify(tokenRepository).save(secondaryToken);
    verify(tokenRepository).save(usedPrimaryToken);
  }

  @Test
  void validatePrimaryToken_tokenWithInvalidUUIDFormat_throwsInvalidTokenException() {
    // Given
    var invalidUuid = "not-a-uuid";

    // When & Then
    var ex = assertThrows(InvalidTokenException.class,
        () -> validateTokenUseCase.validatePrimaryToken(invalidUuid));

    assertTrue(ex.getMessage().contains("Token format is invalid"));
  }

  @Test
  void validatePrimaryToken_secondaryTokenCreatedHasCorrectTypeAndStatus() {
    // Given
    when(tokenRepository.findByToken(PRIMARY_TOKEN)).thenReturn(Optional.of(primaryToken));
    var secondaryTokenUuid = "c1d2e3f4-5678-9101-1121-314151617181";
    var secondaryToken = createSecondaryToken(
        secondaryTokenUuid,
        EMAIL,
        ACCOUNT_ID,
        NOW_INSTANT,
        600);
    when(tokenFactory.createToken(ACCOUNT_ID, EMAIL)).thenReturn(secondaryToken);
    when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(secondaryToken);

    // When
    var result = validateTokenUseCase.validatePrimaryToken(PRIMARY_TOKEN);

    // Then
    assertEquals(TokenType.SECONDARY, secondaryToken.getType());
    assertEquals(TokenStatus.ACTIVE, secondaryToken.getStatus());
    assertEquals(UUID.fromString(secondaryTokenUuid), result.newToken());
  }

  @Test
  void validatePrimaryToken_multipleCalls_producesDifferentSecondaryTokens() {
    // Given
    when(tokenRepository.findByToken(PRIMARY_TOKEN)).thenReturn(Optional.of(primaryToken));
    var secondaryToken1 = createSecondaryToken(
        UUID.randomUUID().toString(),
        EMAIL,
        ACCOUNT_ID,
        NOW_INSTANT,
        600);
    var secondaryToken2 = createSecondaryToken(
        UUID.randomUUID().toString(),
        EMAIL,
        ACCOUNT_ID,
        NOW_INSTANT,
        600);
    when(tokenFactory.createToken(ACCOUNT_ID, EMAIL))
        .thenReturn(secondaryToken1)
        .thenReturn(secondaryToken2);
    when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    var result1 = validateTokenUseCase.validatePrimaryToken(PRIMARY_TOKEN);
    var result2 = validateTokenUseCase.validatePrimaryToken(PRIMARY_TOKEN);

    // Then
    assertNotEquals(result1.newToken(), result2.newToken());
  }

  @Test
  void validatePrimaryToken_tokenWithFutureCreatedAt_acceptsToken() {
    // Given
    var futureCreatedToken = primaryToken.toBuilder()
        .createdAt(OffsetDateTime.ofInstant(NOW_INSTANT, ZoneOffset.UTC).plusDays(1))
        .expiresAt(OffsetDateTime.ofInstant(NOW_INSTANT, ZoneOffset.UTC).plusDays(1).plusMinutes(10))
        .build();
    var usedFutureCreatedToken = futureCreatedToken.toBuilder()
        .status(TokenStatus.USED)
        .build();
    var secondaryToken = createSecondaryToken(
        "b3c1f1b3-477e-45c2-9f3c-cccfbb8cd44e",
        EMAIL,
        ACCOUNT_ID,
        NOW_INSTANT,
        600);

    when(tokenRepository.findByToken(PRIMARY_TOKEN)).thenReturn(Optional.of(futureCreatedToken));
    when(tokenFactory.createToken(ACCOUNT_ID, EMAIL)).thenReturn(secondaryToken);
    when(tokenRepository.save(usedFutureCreatedToken)).thenReturn(usedFutureCreatedToken);
    when(tokenRepository.save(secondaryToken)).thenReturn(secondaryToken);

    // When
    var result = validateTokenUseCase.validatePrimaryToken(PRIMARY_TOKEN);

    // Then
    assertNotNull(result);
    assertTrue(result.valid());
    assertEquals(UUID.fromString(PRIMARY_TOKEN), result.token());
    assertEquals(UUID.fromString(secondaryToken.getToken()), result.newToken());
    assertEquals("password_reset", result.tokenType());

    verify(tokenRepository).save(usedFutureCreatedToken);
    verify(tokenRepository).save(secondaryToken);
  }

  @Test
  void validatePrimaryToken_secondaryTokenExpiryIsLaterThanPrimaryToken() {
    // Given
    when(tokenRepository.findByToken(PRIMARY_TOKEN)).thenReturn(Optional.of(primaryToken));
    var secondaryToken = createSecondaryToken(
        UUID.randomUUID().toString(),
        EMAIL,
        ACCOUNT_ID,
        NOW_INSTANT,
        1200);
    when(tokenFactory.createToken(ACCOUNT_ID, EMAIL)).thenReturn(secondaryToken);
    when(tokenRepository.save(any(PasswordResetToken.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // When
    var result = validateTokenUseCase.validatePrimaryToken(PRIMARY_TOKEN);

    // Then
    assertTrue(result.expiresAt().isAfter(primaryToken.getExpiresAt().toInstant()));
  }
}
