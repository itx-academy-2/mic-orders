package com.academy.orders.application.passwordreset.usecase;

import com.academy.orders.domain.account.entity.Account;
import com.academy.orders.domain.account.factory.PasswordResetTokenFactoryUseCase;
import com.academy.orders.domain.account.repository.AccountRepository;
import com.academy.orders.domain.account.usecase.PasswordResetEmailSenderUseCase;
import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.repository.PasswordResetTokenRepository;
import com.academy.orders.domain.passwordreset.usecase.SendPasswordResetEmailUseCase;
import java.time.Clock;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link SendPasswordResetEmailUseCase} responsible for sending password reset emails to users.
 *
 * <p>This class handles the logic of finding the account by email, checking cooldown periods between reset requests, generating password
 * reset tokens, and sending the reset email.</p>
 *
 * <p>Marked as thread-safe and transactional.</p>
 */
@Slf4j
@Service
@Transactional
public class SendPasswordResetEmailUseCaseImpl implements SendPasswordResetEmailUseCase {
  private final AccountRepository accountRepository;

  private final PasswordResetTokenRepository tokenRepository;

  private final PasswordResetTokenFactoryUseCase tokenFactory;

  private final PasswordResetEmailSenderUseCase emailSender;

  private final Clock clock;

  private static final Duration COOLDOWN_DURATION = Duration.ofMinutes(2);

  public SendPasswordResetEmailUseCaseImpl(
      AccountRepository accountRepository,
      PasswordResetTokenRepository tokenRepository,
      @Qualifier("primaryTokenFactory") PasswordResetTokenFactoryUseCase tokenFactory,
      PasswordResetEmailSenderUseCase emailSender,
      Clock clock) {
    this.accountRepository = accountRepository;
    this.tokenRepository = tokenRepository;
    this.tokenFactory = tokenFactory;
    this.emailSender = emailSender;
    this.clock = clock;
  }

  /**
   * Sends a password reset email if the account exists and the cooldown period has passed since the last reset request.
   *
   * @param email the user's email address requesting password reset
   */
  @Override
  public void sendResetEmail(String email) {
    var account = findAccountByEmail(email);
    if (account == null) {
      log.debug("No account found for email: {}. Silently returning.", email);
      return;
    }
    if (isWithinCooldownPeriod(account.id())) {
      log.info("Password reset request within cooldown period for account: {}", account.id());
      return;
    }
    createAndSendPasswordResetToken(account, email);
  }

  /**
   * Finds an account by the provided email.
   *
   * @param email the email to search for
   * @return the account if found, otherwise null
   */
  private Account findAccountByEmail(String email) {
    return accountRepository.findAccountByEmail(email).orElse(null);
  }

  /**
   * Checks whether the latest primary token for the account is still within the cooldown period.
   *
   * @param accountId the ID of the account
   * @return true if cooldown period is active, false otherwise
   */
  private boolean isWithinCooldownPeriod(Long accountId) {
    return tokenRepository.findLatestPrimaryTokenByAccountId(accountId)
        .map(this::isTokenWithinCooldown)
        .orElse(false);
  }

  /**
   * Determines if the given token is still valid and within the cooldown duration.
   *
   * @param token the password reset token
   * @return true if token is not expired and cooldown duration is not exceeded, false otherwise
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
   * Creates a new password reset token, saves it to the repository, and sends the reset email to the user.
   *
   * @param account the user's account
   * @param email the user's email address
   */
  private void createAndSendPasswordResetToken(Account account, String email) {
    var token = tokenFactory.createToken(account.id(), email);
    tokenRepository.save(token);
    emailSender.send(email, token.getToken());
  }
}
