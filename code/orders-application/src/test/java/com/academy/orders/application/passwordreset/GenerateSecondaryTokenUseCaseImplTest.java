package com.academy.orders.application.passwordreset;

import static com.academy.orders.application.ModelUtils.*;
import com.academy.orders.application.passwordreset.usecase.GenerateSecondaryTokenUseCaseImpl;
import com.academy.orders.domain.account.factory.PasswordResetTokenFactory;
import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import com.academy.orders.domain.passwordreset.exception.InvalidTokenException;
import com.academy.orders.domain.passwordreset.exception.TokenNotFoundException;
import com.academy.orders.domain.passwordreset.repository.PasswordResetTokenRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

@ExtendWith(MockitoExtension.class)
class GenerateSecondaryTokenUseCaseImplTest {
  @Mock
  private PasswordResetTokenRepository tokenRepository;

  @Mock
  @Qualifier("primaryTokenFactory")
  private PasswordResetTokenFactory tokenFactory;

  @InjectMocks
  private GenerateSecondaryTokenUseCaseImpl generateSecondaryTokenUseCase;

  static final String PRIMARY_TOKEN_VALUE = "primary-token-123";

  static final String SECONDARY_TOKEN_VALUE = "secondary-token-456";

  static final String TEST_EMAIL = "test@example.com";

  static final Long ACCOUNT_ID = 1L;

  static final Instant FIXED_INSTANT = Instant.parse("2025-07-05T12:00:00Z");

  private PasswordResetToken primaryToken;

  private PasswordResetToken secondaryToken;

  @BeforeEach
  void setUp() {
    var clock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
    primaryToken = createPrimaryToken(PRIMARY_TOKEN_VALUE, TEST_EMAIL, ACCOUNT_ID, FIXED_INSTANT, 600);
    secondaryToken = createSecondaryToken(SECONDARY_TOKEN_VALUE, TEST_EMAIL, ACCOUNT_ID, FIXED_INSTANT, 600);
    generateSecondaryTokenUseCase = new GenerateSecondaryTokenUseCaseImpl(tokenRepository, tokenFactory, clock);
  }

  @Test
  void generateSecondaryToken_ValidPrimaryToken_ReturnsSecondaryTokenValue() {
    // given
    when(tokenRepository.findByToken(PRIMARY_TOKEN_VALUE)).thenReturn(Optional.of(primaryToken));
    var usedPrimaryToken = createUsedPrimaryToken(primaryToken);
    when(tokenRepository.save(usedPrimaryToken)).thenReturn(usedPrimaryToken);
    when(tokenFactory.createToken(ACCOUNT_ID, TEST_EMAIL)).thenReturn(secondaryToken);
    when(tokenRepository.save(secondaryToken)).thenReturn(secondaryToken);

    // when
    var result = generateSecondaryTokenUseCase.generateSecondaryToken(PRIMARY_TOKEN_VALUE);

    // then
    assertEquals(SECONDARY_TOKEN_VALUE, result);
    verify(tokenRepository).findByToken(PRIMARY_TOKEN_VALUE);
    verify(tokenFactory).createToken(ACCOUNT_ID, TEST_EMAIL);
    verify(tokenRepository).save(usedPrimaryToken);
    verify(tokenRepository).save(secondaryToken);
    verifyNoMoreInteractions(tokenRepository, tokenFactory);
  }

  @Test
  void generateSecondaryToken_TokenNotFound_ThrowsTokenNotFoundException() {
    // given
    when(tokenRepository.findByToken(PRIMARY_TOKEN_VALUE)).thenReturn(Optional.empty());

    // when
    var exception = assertThrows(TokenNotFoundException.class,
        () -> generateSecondaryTokenUseCase.generateSecondaryToken(PRIMARY_TOKEN_VALUE));

    // then
    assertEquals(PRIMARY_TOKEN_VALUE, exception.getMessage());
    verify(tokenRepository).findByToken(PRIMARY_TOKEN_VALUE);
    verifyNoInteractions(tokenFactory);
    verifyNoMoreInteractions(tokenRepository);
  }

  @Test
  void generateSecondaryToken_NonPrimaryToken_ThrowsInvalidTokenException() {
    // given
    var nonPrimaryToken = createSecondaryToken(PRIMARY_TOKEN_VALUE, TEST_EMAIL, ACCOUNT_ID, FIXED_INSTANT, 600);
    when(tokenRepository.findByToken(PRIMARY_TOKEN_VALUE)).thenReturn(Optional.of(nonPrimaryToken));

    // when
    var exception = assertThrows(InvalidTokenException.class,
        () -> generateSecondaryTokenUseCase.generateSecondaryToken(PRIMARY_TOKEN_VALUE));

    // then
    assertEquals("Token is not primary", exception.getMessage());
    verify(tokenRepository).findByToken(PRIMARY_TOKEN_VALUE);
    verifyNoInteractions(tokenFactory);
    verifyNoMoreInteractions(tokenRepository);
  }

  @Test
  void generateSecondaryToken_UsedToken_ThrowsInvalidTokenException() {
    // given
    var usedToken = primaryToken.markAsUsed();
    when(tokenRepository.findByToken(PRIMARY_TOKEN_VALUE)).thenReturn(Optional.of(usedToken));

    // when
    var exception = assertThrows(InvalidTokenException.class,
        () -> generateSecondaryTokenUseCase.generateSecondaryToken(PRIMARY_TOKEN_VALUE));

    // then
    assertEquals("Token has already been used", exception.getMessage());
    verify(tokenRepository).findByToken(PRIMARY_TOKEN_VALUE);
    verifyNoInteractions(tokenFactory);
    verifyNoMoreInteractions(tokenRepository);
  }

  @Test
  void generateSecondaryToken_ExpiredToken_ThrowsInvalidTokenException() {
    // given
    var expiredToken = createExpiredToken(PRIMARY_TOKEN_VALUE, TEST_EMAIL, ACCOUNT_ID, FIXED_INSTANT.minusSeconds(600));
    when(tokenRepository.findByToken(PRIMARY_TOKEN_VALUE)).thenReturn(Optional.of(expiredToken));

    // when
    var exception = assertThrows(InvalidTokenException.class,
        () -> generateSecondaryTokenUseCase.generateSecondaryToken(PRIMARY_TOKEN_VALUE));

    // then
    assertEquals("Token has expired", exception.getMessage());
    verify(tokenRepository).findByToken(PRIMARY_TOKEN_VALUE);
    verifyNoInteractions(tokenFactory);
    verifyNoMoreInteractions(tokenRepository);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "   "})
  void generateSecondaryToken_InvalidTokenValue_ThrowsInvalidTokenException(String invalidToken) {
    // given
    // when
    var exception = assertThrows(InvalidTokenException.class,
        () -> generateSecondaryTokenUseCase.generateSecondaryToken(invalidToken));

    // then
    if (invalidToken == null) {
      assertEquals("Token value cannot be null", exception.getMessage());
    } else {
      assertEquals("Token value cannot be empty or blank", exception.getMessage());
    }
    verifyNoInteractions(tokenRepository, tokenFactory);
  }

  @Test
  void generateSecondaryToken_TokenRepositoryThrowsException_PropagatesException() {
    // given
    when(tokenRepository.findByToken(PRIMARY_TOKEN_VALUE)).thenThrow(new RuntimeException("Database error"));

    // when
    var exception = assertThrows(RuntimeException.class,
        () -> generateSecondaryTokenUseCase.generateSecondaryToken(PRIMARY_TOKEN_VALUE));

    // then
    assertEquals("Database error", exception.getMessage());
    verify(tokenRepository).findByToken(PRIMARY_TOKEN_VALUE);
    verifyNoInteractions(tokenFactory);
    verifyNoMoreInteractions(tokenRepository);
  }

  @Test
  void generateSecondaryToken_NullSecondaryTokenValue_ThrowsInvalidTokenException() {
    // given
    var nullToken = createTokenWithNullValue(TEST_EMAIL, ACCOUNT_ID);
    when(tokenRepository.findByToken(PRIMARY_TOKEN_VALUE)).thenReturn(Optional.of(primaryToken));
    when(tokenRepository.save(argThat(token -> token.getType() == TokenType.PRIMARY))).thenReturn(primaryToken);
    when(tokenFactory.createToken(ACCOUNT_ID, TEST_EMAIL)).thenReturn(nullToken);

    // when
    var exception = assertThrows(InvalidTokenException.class,
        () -> generateSecondaryTokenUseCase.generateSecondaryToken(PRIMARY_TOKEN_VALUE));

    // then
    assertEquals("Secondary token value cannot be null", exception.getMessage());
    verify(tokenRepository).findByToken(PRIMARY_TOKEN_VALUE);
    verify(tokenRepository).save(argThat(token -> token.getType() == TokenType.PRIMARY));
    verify(tokenFactory).createToken(ACCOUNT_ID, TEST_EMAIL);
    verify(tokenRepository, never()).save(argThat(token -> token.getType() == TokenType.SECONDARY));
    verifyNoMoreInteractions(tokenRepository, tokenFactory);
  }

  @Test
  void generateSecondaryToken_TokenRepositorySaveThrowsException_PropagatesException() {
    // given
    when(tokenRepository.findByToken(PRIMARY_TOKEN_VALUE)).thenReturn(Optional.of(primaryToken));
    var usedPrimaryToken = createUsedPrimaryToken(primaryToken);
    when(tokenRepository.save(usedPrimaryToken)).thenReturn(usedPrimaryToken);
    when(tokenFactory.createToken(ACCOUNT_ID, TEST_EMAIL)).thenReturn(secondaryToken);
    when(tokenRepository.save(secondaryToken)).thenThrow(new RuntimeException("Save error"));

    // when
    var exception = assertThrows(RuntimeException.class,
        () -> generateSecondaryTokenUseCase.generateSecondaryToken(PRIMARY_TOKEN_VALUE));

    // then
    assertEquals("Save error", exception.getMessage());
    verify(tokenRepository).findByToken(PRIMARY_TOKEN_VALUE);
    verify(tokenFactory).createToken(ACCOUNT_ID, TEST_EMAIL);
    verify(tokenRepository).save(usedPrimaryToken);
    verify(tokenRepository).save(secondaryToken);
  }

  @Test
  void generateSecondaryToken_ExpirationBoundary_ThrowsInvalidTokenException() {
    // given
    var expiredToken = createExpiredToken(PRIMARY_TOKEN_VALUE, TEST_EMAIL, ACCOUNT_ID, FIXED_INSTANT.minusSeconds(1));
    when(tokenRepository.findByToken(PRIMARY_TOKEN_VALUE)).thenReturn(Optional.of(expiredToken));

    // when
    var exception = assertThrows(InvalidTokenException.class,
        () -> generateSecondaryTokenUseCase.generateSecondaryToken(PRIMARY_TOKEN_VALUE));

    // then
    assertEquals("Token has expired", exception.getMessage());
    verify(tokenRepository).findByToken(PRIMARY_TOKEN_VALUE);
    verifyNoInteractions(tokenFactory);
    verifyNoMoreInteractions(tokenRepository);
  }
}
