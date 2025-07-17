package com.academy.orders.application.passwordreset.usecase;

import com.academy.orders.domain.account.entity.Account;
import com.academy.orders.domain.account.factory.PasswordResetTokenFactory;
import com.academy.orders.domain.account.repository.AccountRepository;
import com.academy.orders.domain.account.usecase.PasswordResetEmailSender;
import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.repository.PasswordResetTokenRepository;
import com.academy.orders.domain.passwordreset.usecase.SendPasswordResetEmailUseCase;
import java.time.Clock;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SendPasswordResetEmailUseCaseImpl implements SendPasswordResetEmailUseCase {
  private final AccountRepository accountRepository;

  private final PasswordResetTokenRepository tokenRepository;

  private final PasswordResetTokenFactory tokenFactory;

  private final PasswordResetEmailSender emailSender;

  private final Clock clock;

  private static final Duration COOLDOWN_DURATION = Duration.ofMinutes(2);

  /**
   * Constructs a new instance of SendPasswordResetEmailUseCaseImpl with the required repositories, token factory, email sender, and clock.
   */
  public SendPasswordResetEmailUseCaseImpl(
      AccountRepository accountRepository,
      PasswordResetTokenRepository tokenRepository,
      @Qualifier("primaryTokenFactory") PasswordResetTokenFactory tokenFactory,
      PasswordResetEmailSender emailSender,
      Clock clock) {
    this.accountRepository = accountRepository;
    this.tokenRepository = tokenRepository;
    this.tokenFactory = tokenFactory;
    this.emailSender = emailSender;
    this.clock = clock;
  }

  /**
   * Sends a password reset email to the specified address if an associated account exists and no recent reset token has been issued within the cooldown period.
   *
   * If the account does not exist or a password reset token was recently sent, no action is taken.
   *
   * @param email the email address to send the password reset instructions to
   */
  @Override
  public void sendResetEmail(String email) {
    var account = findAccountByEmail(email);
    if (account == null) {
      return;
    }
    if (isWithinCooldownPeriod(account.id())) {
      return;
    }
    createAndSendPasswordResetToken(account, email);
  }

  /**
   * Retrieves the account associated with the specified email address.
   *
   * @param email the email address to search for
   * @return the Account if found, or null if no account exists for the given email
   */
  private Account findAccountByEmail(String email) {
    return accountRepository.findAccountByEmail(email).orElse(null);
  }

  /**
   * Determines whether the most recent primary password reset token for the specified account is still within the cooldown period.
   *
   * @param accountId the ID of the account to check for cooldown status
   * @return {@code true} if a valid token exists and is within the cooldown period; {@code false} otherwise
   */
  private boolean isWithinCooldownPeriod(Long accountId) {
    return tokenRepository.findLatestPrimaryTokenByAccountId(accountId)
        .map(this::isTokenWithinCooldown)
        .orElse(false);
  }

  /**
   * Determines whether the given password reset token is still within the cooldown period.
   *
   * Returns {@code true} if the token is not expired and was created within the configured cooldown duration; otherwise, returns {@code false}.
   *
   * @param token the password reset token to check
   * @return {@code true} if the token is within the cooldown period, {@code false} otherwise
   */
  private boolean isTokenWithinCooldown(PasswordResetToken token) {
    var now = clock.instant();

    if (token.isExpired(now)) {
      return false;
    }
    var tokenCreatedAt = token.getCreatedAt().toInstant();
    var timeSinceCreation = Duration.between(tokenCreatedAt, now);

    return timeSinceCreation.compareTo(COOLDOWN_DURATION) <= 0;
  }

  /**
   * Creates a new password reset token for the specified account and sends it to the given email address.
   *
   * Generates a token using the account's ID and email, saves it to the repository, and dispatches a password reset email containing the token.
   */
  private void createAndSendPasswordResetToken(Account account, String email) {
    var token = tokenFactory.createToken(account.id(), email);
    tokenRepository.save(token);
    emailSender.send(email, token.getToken());
  }
}
