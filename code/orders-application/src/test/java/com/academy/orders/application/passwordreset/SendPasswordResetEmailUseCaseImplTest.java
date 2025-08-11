package com.academy.orders.application.passwordreset;

import static com.academy.orders.application.ModelUtils.*;
import com.academy.orders.application.passwordreset.usecase.SendPasswordResetEmailUseCaseImpl;
import com.academy.orders.domain.account.entity.Account;
import com.academy.orders.domain.account.factory.PasswordResetTokenFactoryUseCase;
import com.academy.orders.domain.account.repository.AccountRepository;
import com.academy.orders.domain.account.usecase.PasswordResetEmailSenderUseCase;
import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.repository.PasswordResetTokenRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SendPasswordResetEmailUseCaseImplTest {
  @Mock
  private AccountRepository accountRepository;

  @Mock
  private PasswordResetTokenRepository tokenRepository;

  @Mock
  private PasswordResetTokenFactoryUseCase tokenFactory;

  @Mock
  private PasswordResetEmailSenderUseCase emailSender;

  @Mock
  private Clock clock;

  @InjectMocks
  private SendPasswordResetEmailUseCaseImpl sendPasswordResetEmailUseCase;

  static final String TEST_EMAIL = "test@example.com";

  static final Long TEST_ACCOUNT_ID = 1L;

  static final String TEST_TOKEN = UUID.randomUUID().toString();

  static final Instant FIXED_INSTANT = Instant.parse("2023-01-01T10:00:00Z");

  private Account testAccount;

  private PasswordResetToken testToken;

  @BeforeEach
  void setUp() {
    testAccount = createAccount(TEST_ACCOUNT_ID, TEST_EMAIL);
    testToken = createPasswordResetToken(TEST_TOKEN, TEST_EMAIL, TEST_ACCOUNT_ID);
  }

  @Test
  void sendResetEmail_WhenAccountNotFound_ShouldReturnSilently() {
    // Given
    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

    // When
    sendPasswordResetEmailUseCase.sendResetEmail(TEST_EMAIL);

    // Then
    verify(accountRepository).findAccountByEmail(TEST_EMAIL);
    verify(tokenRepository, never()).findLatestPrimaryTokenByAccountId(any());
    verify(tokenFactory, never()).createToken(any(), anyString());
    verify(tokenRepository, never()).save(any());
    verify(emailSender, never()).send(anyString(), anyString());
  }

  @Test
  void sendResetEmail_WhenAccountExists_AndNoRecentToken_ShouldCreateTokenAndSendEmail() {
    // Given
    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAccount));
    when(tokenRepository.findLatestPrimaryTokenByAccountId(TEST_ACCOUNT_ID)).thenReturn(Optional.empty());
    when(tokenFactory.createToken(TEST_ACCOUNT_ID, TEST_EMAIL)).thenReturn(testToken);

    // When
    sendPasswordResetEmailUseCase.sendResetEmail(TEST_EMAIL);

    // Then
    verify(accountRepository).findAccountByEmail(TEST_EMAIL);
    verify(tokenRepository).findLatestPrimaryTokenByAccountId(TEST_ACCOUNT_ID);
    verify(tokenFactory).createToken(TEST_ACCOUNT_ID, TEST_EMAIL);
    verify(tokenRepository).save(testToken);
    verify(emailSender).send(TEST_EMAIL, TEST_TOKEN);
  }

  @Test
  void sendResetEmail_WhenRecentTokenExists_ButExpired_ShouldCreateNewTokenAndSendEmail() {
    // Given
    when(clock.instant()).thenReturn(FIXED_INSTANT);
    var expiredInstant = FIXED_INSTANT.minusSeconds(600);
    var expiredToken = createExpiredToken(TEST_TOKEN, TEST_EMAIL, TEST_ACCOUNT_ID, expiredInstant);

    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAccount));
    when(tokenRepository.findLatestPrimaryTokenByAccountId(TEST_ACCOUNT_ID)).thenReturn(Optional.of(expiredToken));
    when(tokenFactory.createToken(TEST_ACCOUNT_ID, TEST_EMAIL)).thenReturn(testToken);

    // When
    sendPasswordResetEmailUseCase.sendResetEmail(TEST_EMAIL);

    // Then
    verify(tokenFactory).createToken(TEST_ACCOUNT_ID, TEST_EMAIL);
    verify(tokenRepository).save(testToken);
    verify(emailSender).send(TEST_EMAIL, TEST_TOKEN);
  }

  @Test
  void sendResetEmail_WhenRecentTokenExists_AndNotExpired_ButCooldownPassed_ShouldCreateNewToken() {
    // Given
    var tokenCreatedAt = FIXED_INSTANT.minusSeconds(180);
    var recentToken = createPrimaryToken(TEST_TOKEN, TEST_EMAIL, TEST_ACCOUNT_ID, tokenCreatedAt, 600);

    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAccount));
    when(tokenRepository.findLatestPrimaryTokenByAccountId(TEST_ACCOUNT_ID)).thenReturn(Optional.of(recentToken));
    when(tokenFactory.createToken(TEST_ACCOUNT_ID, TEST_EMAIL)).thenReturn(testToken);
    when(clock.instant()).thenReturn(FIXED_INSTANT);

    // When
    sendPasswordResetEmailUseCase.sendResetEmail(TEST_EMAIL);

    // Then
    verify(tokenFactory).createToken(TEST_ACCOUNT_ID, TEST_EMAIL);
    verify(tokenRepository).save(testToken);
    verify(emailSender).send(TEST_EMAIL, TEST_TOKEN);
  }

  @Test
  void sendResetEmail_WhenRecentTokenExists_AndNotExpired_AndCooldownNotPassed_ShouldNotCreateNewToken() {
    // Given
    when(clock.instant()).thenReturn(FIXED_INSTANT);
    var tokenCreatedAt = FIXED_INSTANT.minusSeconds(60);
    var recentToken = createPrimaryToken(TEST_TOKEN, TEST_EMAIL, TEST_ACCOUNT_ID, tokenCreatedAt, 600);

    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAccount));
    when(tokenRepository.findLatestPrimaryTokenByAccountId(TEST_ACCOUNT_ID)).thenReturn(Optional.of(recentToken));

    // When
    sendPasswordResetEmailUseCase.sendResetEmail(TEST_EMAIL);

    // Then
    verify(tokenFactory, never()).createToken(any(), anyString());
    verify(tokenRepository, never()).save(any());
    verify(emailSender, never()).send(anyString(), anyString());
  }

  @Test
  void sendResetEmail_WhenRecentTokenExists_AndCooldownExactly2Minutes_ShouldNotCreateNewToken() {
    // Given
    when(clock.instant()).thenReturn(FIXED_INSTANT);
    var tokenCreatedAt = FIXED_INSTANT.minusSeconds(120);
    var recentToken = createPrimaryToken(TEST_TOKEN, TEST_EMAIL, TEST_ACCOUNT_ID, tokenCreatedAt, 600);

    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAccount));
    when(tokenRepository.findLatestPrimaryTokenByAccountId(TEST_ACCOUNT_ID)).thenReturn(Optional.of(recentToken));

    // When
    sendPasswordResetEmailUseCase.sendResetEmail(TEST_EMAIL);

    // Then
    verify(tokenFactory, never()).createToken(any(), anyString());
    verify(tokenRepository, never()).save(any());
    verify(emailSender, never()).send(anyString(), anyString());
  }

  @Test
  void sendResetEmail_WhenTokenCreatedJustNow_ShouldNotCreateNewToken() {
    // Given
    when(clock.instant()).thenReturn(FIXED_INSTANT);
    var recentToken = createPrimaryToken(TEST_TOKEN, TEST_EMAIL, TEST_ACCOUNT_ID, FIXED_INSTANT, 600);

    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAccount));
    when(tokenRepository.findLatestPrimaryTokenByAccountId(TEST_ACCOUNT_ID)).thenReturn(Optional.of(recentToken));

    // When
    sendPasswordResetEmailUseCase.sendResetEmail(TEST_EMAIL);

    // Then
    verify(tokenFactory, never()).createToken(any(), anyString());
    verify(tokenRepository, never()).save(any());
    verify(emailSender, never()).send(anyString(), anyString());
  }

  @Test
  void sendResetEmail_WhenEmailSenderThrowsException_ShouldPropagateException() {
    // Given
    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAccount));

    var recentToken = createPrimaryToken(TEST_TOKEN, TEST_EMAIL, TEST_ACCOUNT_ID, FIXED_INSTANT.minusSeconds(300), 600);
    when(tokenRepository.findLatestPrimaryTokenByAccountId(TEST_ACCOUNT_ID)).thenReturn(Optional.of(recentToken));

    when(clock.instant()).thenReturn(FIXED_INSTANT);
    when(tokenFactory.createToken(TEST_ACCOUNT_ID, TEST_EMAIL)).thenReturn(testToken);
    doThrow(new RuntimeException("Email sending failed")).when(emailSender).send(TEST_EMAIL, TEST_TOKEN);

    // When & Then
    assertThrows(RuntimeException.class, () -> sendPasswordResetEmailUseCase.sendResetEmail(TEST_EMAIL));

    verify(tokenRepository).save(testToken);
    verify(emailSender).send(TEST_EMAIL, TEST_TOKEN);
  }

  @Test
  void sendResetEmail_WhenTokenRepositoryThrowsException_ShouldPropagateException() {
    // Given
    lenient().when(clock.instant()).thenReturn(FIXED_INSTANT);
    lenient().when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAccount));
    when(tokenRepository.findLatestPrimaryTokenByAccountId(TEST_ACCOUNT_ID)).thenReturn(Optional.empty());
    when(tokenFactory.createToken(TEST_ACCOUNT_ID, TEST_EMAIL)).thenReturn(testToken);
    doThrow(new RuntimeException("Database save failed")).when(tokenRepository).save(testToken);

    // When & Then
    assertThrows(RuntimeException.class, () -> sendPasswordResetEmailUseCase.sendResetEmail(TEST_EMAIL));

    verify(tokenRepository).save(testToken);
    verify(emailSender, never()).send(anyString(), anyString());
  }

  @Test
  void sendResetEmail_WhenNullEmailProvided_ShouldHandleGracefully() {
    // Given
    when(accountRepository.findAccountByEmail(null)).thenReturn(Optional.empty());

    // When
    sendPasswordResetEmailUseCase.sendResetEmail(null);

    // Then
    verify(accountRepository).findAccountByEmail(null);
    verify(tokenRepository, never()).findLatestPrimaryTokenByAccountId(any());
    verify(tokenFactory, never()).createToken(any(), anyString());
    verify(tokenRepository, never()).save(any());
    verify(emailSender, never()).send(anyString(), anyString());
  }

  @Test
  void sendResetEmail_WhenEmptyEmailProvided_ShouldHandleGracefully() {
    // Given
    var emptyEmail = "";
    when(accountRepository.findAccountByEmail(emptyEmail)).thenReturn(Optional.empty());

    // When
    sendPasswordResetEmailUseCase.sendResetEmail(emptyEmail);

    // Then
    verify(accountRepository).findAccountByEmail(emptyEmail);
    verify(tokenRepository, never()).findLatestPrimaryTokenByAccountId(any());
    verify(tokenFactory, never()).createToken(any(), anyString());
    verify(tokenRepository, never()).save(any());
    verify(emailSender, never()).send(anyString(), anyString());
  }

  @Test
  void sendResetEmail_WhenMultipleCallsWithinCooldown_ShouldOnlyCreateOneToken() {
    // Given
    when(clock.instant()).thenReturn(FIXED_INSTANT);
    var tokenCreatedAt = FIXED_INSTANT.minusSeconds(60);
    var recentToken = createPrimaryToken(TEST_TOKEN, TEST_EMAIL, TEST_ACCOUNT_ID, tokenCreatedAt, 600);

    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAccount));
    when(tokenRepository.findLatestPrimaryTokenByAccountId(TEST_ACCOUNT_ID)).thenReturn(Optional.of(recentToken));

    // When
    sendPasswordResetEmailUseCase.sendResetEmail(TEST_EMAIL);
    sendPasswordResetEmailUseCase.sendResetEmail(TEST_EMAIL);
    sendPasswordResetEmailUseCase.sendResetEmail(TEST_EMAIL);

    // Then
    verify(accountRepository, times(3)).findAccountByEmail(TEST_EMAIL);
    verify(tokenRepository, times(3)).findLatestPrimaryTokenByAccountId(TEST_ACCOUNT_ID);
    verify(tokenFactory, never()).createToken(any(), anyString());
    verify(tokenRepository, never()).save(any());
    verify(emailSender, never()).send(anyString(), anyString());
  }

  @Test
  void sendResetEmail_WhenTokenHasNullCreatedAt_ShouldThrowNullPointerException() {
    var tokenWithNullCreatedAt = createTokenWithNullValue(TEST_EMAIL, TEST_ACCOUNT_ID);

    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(testAccount));
    when(tokenRepository.findLatestPrimaryTokenByAccountId(TEST_ACCOUNT_ID)).thenReturn(Optional.of(tokenWithNullCreatedAt));

    assertThrows(NullPointerException.class, () -> sendPasswordResetEmailUseCase.sendResetEmail(TEST_EMAIL));
  }
}
