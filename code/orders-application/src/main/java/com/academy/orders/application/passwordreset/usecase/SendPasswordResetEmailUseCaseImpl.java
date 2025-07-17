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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class SendPasswordResetEmailUseCaseImpl implements SendPasswordResetEmailUseCase {
  private final AccountRepository accountRepository;

  private final PasswordResetTokenRepository tokenRepository;

  private final PasswordResetTokenFactory tokenFactory;

  private final PasswordResetEmailSender emailSender;

  private final Clock clock;

  private static final Duration COOLDOWN_DURATION = Duration.ofMinutes(2);

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

  private Account findAccountByEmail(String email) {
    return accountRepository.findAccountByEmail(email).orElse(null);
  }

  private boolean isWithinCooldownPeriod(Long accountId) {
    return tokenRepository.findLatestPrimaryTokenByAccountId(accountId)
        .map(this::isTokenWithinCooldown)
        .orElse(false);
  }

  private boolean isTokenWithinCooldown(PasswordResetToken token) {
    var now = clock.instant();

    if (token.isExpired(now)) {
      return false;
    }
    var tokenCreatedAt = token.getCreatedAt().toInstant();
    var timeSinceCreation = Duration.between(tokenCreatedAt, now);

    return timeSinceCreation.compareTo(COOLDOWN_DURATION) <= 0;
  }

  private void createAndSendPasswordResetToken(Account account, String email) {
    var token = tokenFactory.createToken(account.id(), email);
    tokenRepository.save(token);
    emailSender.send(email, token.getToken());
  }
}
