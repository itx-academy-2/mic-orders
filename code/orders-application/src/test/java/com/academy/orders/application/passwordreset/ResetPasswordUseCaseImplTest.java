package com.academy.orders.application.passwordreset;

import static com.academy.orders.application.ModelUtils.*;
import com.academy.orders.application.passwordreset.usecase.ResetPasswordUseCaseImpl;
import com.academy.orders.domain.account.entity.Account;
import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.account.repository.AccountRepository;
import com.academy.orders.domain.passwordreset.dto.PasswordResetRequestDTO;
import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenStatus;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import com.academy.orders.domain.passwordreset.exception.InvalidPasswordException;
import com.academy.orders.domain.passwordreset.exception.InvalidTokenException;
import com.academy.orders.domain.passwordreset.exception.TokenNotFoundException;
import com.academy.orders.domain.passwordreset.repository.PasswordResetTokenRepository;
import com.academy.orders.domain.passwordreset.usecase.PasswordHashingPort;
import java.time.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResetPasswordUseCaseImplTest {
  @Mock
  private PasswordResetTokenRepository tokenRepository;

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private PasswordHashingPort passwordHashingPort;

  @InjectMocks
  private ResetPasswordUseCaseImpl resetPasswordUseCase;

  static final String TEST_EMAIL = "test@example.com";

  static final String NEW_PASSWORD = "newPassword123";

  static final String HASHED_PASSWORD = "hashedPassword123";

  static final Long ACCOUNT_ID = 1L;

  static final Instant FIXED_INSTANT = Instant.parse("2025-07-05T12:00:00Z");

  private static final String TEST_TOKEN = "550e8400-e29b-41d4-a716-446655440000";

  private PasswordResetRequestDTO validCommand;

  private PasswordResetToken validToken;

  private Account account;

  @BeforeEach
  void setUp() {
    var clock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
    resetPasswordUseCase = new ResetPasswordUseCaseImpl(tokenRepository, accountRepository, passwordHashingPort, clock);

    validCommand = createPasswordResetCommand(TEST_TOKEN, NEW_PASSWORD);

    validToken = createPasswordResetToken(
        TEST_TOKEN,
        TEST_EMAIL,
        TokenType.SECONDARY,
        TokenStatus.ACTIVE,
        OffsetDateTime.ofInstant(FIXED_INSTANT, ZoneOffset.UTC).plusMinutes(10),
        ACCOUNT_ID);

    account = createAccount(ACCOUNT_ID, TEST_EMAIL, "oldPassword");
  }

  @Test
  void resetPassword_Success() {
    // given
    when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(validToken));
    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(account));
    when(passwordHashingPort.hash(NEW_PASSWORD)).thenReturn(HASHED_PASSWORD);
    var usedToken = validToken.markAsUsed();
    when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(usedToken);

    // when
    assertDoesNotThrow(() -> resetPasswordUseCase.resetPassword(validCommand));

    // then
    verify(tokenRepository).findByToken(TEST_TOKEN);
    verify(accountRepository).findAccountByEmail(TEST_EMAIL);
    verify(passwordHashingPort).hash(NEW_PASSWORD);
    verify(accountRepository).updatePassword(ACCOUNT_ID, HASHED_PASSWORD);
    verify(tokenRepository).save(argThat(PasswordResetToken::isUsed));
    verify(tokenRepository).deleteAllByStatus(TokenStatus.USED);
  }

  @Test
  void resetPassword_TokenNotFound_ThrowsTokenNotFoundException() {
    // given
    when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.empty());

    // when
    var exception = assertThrows(TokenNotFoundException.class,
        () -> resetPasswordUseCase.resetPassword(validCommand));

    // then
    assertEquals(TEST_TOKEN, exception.getMessage());
    verify(tokenRepository).findByToken(TEST_TOKEN);
    verifyNoInteractions(accountRepository, passwordHashingPort);
    verifyNoMoreInteractions(tokenRepository);
  }

  @Test
  void resetPassword_TokenIsNotSecondary_ThrowsInvalidTokenException() {
    // given
    var primaryToken = validToken.toBuilder()
        .type(TokenType.PRIMARY)
        .build();
    when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(primaryToken));

    // when
    var exception = assertThrows(InvalidTokenException.class,
        () -> resetPasswordUseCase.resetPassword(validCommand));

    // then
    assertEquals("Token is not secondary", exception.getMessage());
    verify(tokenRepository).findByToken(TEST_TOKEN);
    verifyNoInteractions(accountRepository, passwordHashingPort);
    verifyNoMoreInteractions(tokenRepository);
  }

  @Test
  void resetPassword_TokenAlreadyUsed_ThrowsInvalidTokenException() {
    // given
    var usedToken = validToken.markAsUsed();
    when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(usedToken));

    // when
    var exception = assertThrows(InvalidTokenException.class,
        () -> resetPasswordUseCase.resetPassword(validCommand));

    // then
    assertEquals("Token has already been used", exception.getMessage());
    verify(tokenRepository).findByToken(TEST_TOKEN);
    verifyNoInteractions(accountRepository, passwordHashingPort);
    verifyNoMoreInteractions(tokenRepository);
  }

  @Test
  void resetPassword_TokenExpired_ThrowsInvalidTokenException() {
    // given
    var expiredToken = createTokenWithExpiry(
        TEST_TOKEN,
        TEST_EMAIL,
        TokenType.SECONDARY,
        TokenStatus.ACTIVE,
        FIXED_INSTANT.minus(Duration.ofHours(1)),
        ACCOUNT_ID);
    when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(expiredToken));

    // when
    var exception = assertThrows(InvalidTokenException.class,
        () -> resetPasswordUseCase.resetPassword(validCommand));

    // then
    assertEquals("Token has expired", exception.getMessage());
    verify(tokenRepository).findByToken(TEST_TOKEN);
    verifyNoInteractions(accountRepository, passwordHashingPort);
    verifyNoMoreInteractions(tokenRepository);
  }

  @Test
  void resetPassword_AccountNotFound_ThrowsAccountNotFoundException() {
    // given
    when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(validToken));
    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

    // when
    var exception = assertThrows(AccountNotFoundException.class,
        () -> resetPasswordUseCase.resetPassword(validCommand));

    // then
    assertEquals("Account with email: " + TEST_EMAIL + " is not found", exception.getMessage());
    verify(tokenRepository).findByToken(TEST_TOKEN);
    verify(accountRepository).findAccountByEmail(TEST_EMAIL);
    verifyNoInteractions(passwordHashingPort);
    verifyNoMoreInteractions(tokenRepository);
  }

  @Test
  void resetPassword_UpdatePasswordCalled_WithHashedPassword() {
    // given
    when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(validToken));
    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(account));
    when(passwordHashingPort.hash(NEW_PASSWORD)).thenReturn(HASHED_PASSWORD);
    var usedToken = validToken.markAsUsed();
    when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(usedToken);

    // when
    resetPasswordUseCase.resetPassword(validCommand);

    // then
    verify(passwordHashingPort).hash(NEW_PASSWORD);
    verify(accountRepository).updatePassword(ACCOUNT_ID, HASHED_PASSWORD);
  }

  @Test
  void resetPassword_TokenMarkedAsUsedAndDeleted_AfterSuccessfulReset() {
    // given
    when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(validToken));
    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(account));
    when(passwordHashingPort.hash(NEW_PASSWORD)).thenReturn(HASHED_PASSWORD);
    var usedToken = validToken.markAsUsed();
    when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(usedToken);

    // when
    resetPasswordUseCase.resetPassword(validCommand);

    // then
    verify(tokenRepository).save(argThat(token -> token.isUsed() && token.getToken().equals(TEST_TOKEN)));
    verify(tokenRepository).deleteAllByStatus(TokenStatus.USED);
  }

  @Test
  void resetPassword_AllValidationsPassed_InCorrectOrder() {
    // given
    when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(validToken));
    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(account));
    when(passwordHashingPort.hash(NEW_PASSWORD)).thenReturn(HASHED_PASSWORD);
    var usedToken = validToken.markAsUsed();
    when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(usedToken);

    // when
    resetPasswordUseCase.resetPassword(validCommand);

    // then
    var inOrder = inOrder(tokenRepository, accountRepository, passwordHashingPort);
    inOrder.verify(tokenRepository).findByToken(TEST_TOKEN);
    inOrder.verify(accountRepository).findAccountByEmail(TEST_EMAIL);
    inOrder.verify(passwordHashingPort).hash(NEW_PASSWORD);
    inOrder.verify(accountRepository).updatePassword(ACCOUNT_ID, HASHED_PASSWORD);
    inOrder.verify(tokenRepository).save(any(PasswordResetToken.class));
    inOrder.verify(tokenRepository).deleteAllByStatus(TokenStatus.USED);
  }

  @Test
  void resetPassword_WithNullCommand_ThrowsException() {
    // when & then
    assertThrows(NullPointerException.class,
        () -> resetPasswordUseCase.resetPassword(null));
  }

  @Test
  void resetPassword_WithNullToken_ThrowsInvalidTokenException() {
    var commandWithNullToken = createPasswordResetCommand(null, NEW_PASSWORD);

    assertThrows(InvalidTokenException.class,
        () -> resetPasswordUseCase.resetPassword(commandWithNullToken));
  }

  @Test
  void resetPassword_WithNullPassword_ThrowsException() {
    // given
    var commandWithNullPassword = createPasswordResetCommand(TEST_TOKEN, null);

    // when & then
    assertThrows(InvalidPasswordException.class,
        () -> resetPasswordUseCase.resetPassword(commandWithNullPassword));
  }

  @Test
  void resetPassword_WithEmptyPassword_ThrowsInvalidPasswordException() {
    // given
    var commandWithEmptyPassword = createPasswordResetCommand(TEST_TOKEN, "");

    // when
    var exception = assertThrows(InvalidPasswordException.class,
        () -> resetPasswordUseCase.resetPassword(commandWithEmptyPassword));

    // then
    assertEquals("Password cannot be null or empty", exception.getMessage());
    verifyNoInteractions(tokenRepository, accountRepository, passwordHashingPort);
  }

  @Test
  void resetPassword_WithWhitespacePassword_ThrowsInvalidPasswordException() {
    // given
    var commandWithWhitespacePassword = createPasswordResetCommand(TEST_TOKEN, "   ");

    // when
    var exception = assertThrows(InvalidPasswordException.class,
        () -> resetPasswordUseCase.resetPassword(commandWithWhitespacePassword));

    // then
    assertEquals("Password cannot be null or empty", exception.getMessage());
    verifyNoInteractions(tokenRepository, accountRepository, passwordHashingPort);
  }

  @Test
  void resetPassword_TokenExpiresAtCurrentTime_ThrowsInvalidTokenException() {
    // given
    var boundaryToken = createTokenWithExpiry(
        TEST_TOKEN,
        TEST_EMAIL,
        TokenType.SECONDARY,
        TokenStatus.ACTIVE,
        FIXED_INSTANT.minusSeconds(1),
        ACCOUNT_ID);
    when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(boundaryToken));

    // when
    var exception = assertThrows(InvalidTokenException.class,
        () -> resetPasswordUseCase.resetPassword(validCommand));

    // then
    assertEquals("Token has expired", exception.getMessage());
    verify(tokenRepository).findByToken(TEST_TOKEN);
    verifyNoInteractions(accountRepository, passwordHashingPort);
  }

  @Test
  void resetPassword_MultipleTokenTypes_OnlySecondaryAllowed() {
    for (TokenType type : TokenType.values()) {
      if (type != TokenType.SECONDARY) {
        // given
        PasswordResetToken tokenWithType = validToken.toBuilder()
            .type(type)
            .build();
        when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(tokenWithType));

        // when & then
        var exception = assertThrows(InvalidTokenException.class,
            () -> resetPasswordUseCase.resetPassword(validCommand));
        assertEquals("Token is not secondary", exception.getMessage());

        reset(tokenRepository);
      }
    }
  }

  @Test
  void resetPassword_TokenActive_IsNotUsed() {
    // given
    var activeToken = validToken.toBuilder()
        .status(TokenStatus.ACTIVE)
        .build();
    when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(activeToken));
    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(account));
    when(passwordHashingPort.hash(NEW_PASSWORD)).thenReturn(HASHED_PASSWORD);
    var usedToken = activeToken.markAsUsed();
    when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(usedToken);

    // when
    assertDoesNotThrow(() -> resetPasswordUseCase.resetPassword(validCommand));

    // then
    assertFalse(activeToken.isUsed());
    verify(tokenRepository).save(argThat(PasswordResetToken::isUsed));
    verify(tokenRepository).deleteAllByStatus(TokenStatus.USED);
  }
}
