package com.academy.orders.application.passwordreset.usecase;

import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.account.repository.AccountRepository;
import com.academy.orders.domain.passwordreset.dto.PasswordResetRequestDTO;
import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import com.academy.orders.domain.passwordreset.exception.InvalidPasswordException;
import com.academy.orders.domain.passwordreset.exception.InvalidTokenException;
import com.academy.orders.domain.passwordreset.exception.TokenNotFoundException;
import com.academy.orders.domain.passwordreset.repository.PasswordResetTokenRepository;
import com.academy.orders.domain.passwordreset.usecase.PasswordHashingPort;
import com.academy.orders.domain.passwordreset.usecase.ResetPasswordUseCase;
import java.time.Clock;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordUseCaseImpl implements ResetPasswordUseCase {
  static final String TOKEN_NOT_SECONDARY_MSG = "Token is not secondary";

  static final String TOKEN_USED_MSG = "Token has already been used";

  static final String TOKEN_EXPIRED_MSG = "Token has expired";

  static final String INVALID_PASSWORD_MSG = "Password cannot be null or empty";

  private final PasswordResetTokenRepository tokenRepository;

  private final AccountRepository accountRepository;

  private final PasswordHashingPort passwordHashingPort;

  private final Clock clock;

  @Override
  public void resetPassword(PasswordResetRequestDTO command) {
    validatePassword(command);
    var token = validateAndGetToken(command);
    updateAccountPassword(token, command);
    markTokenAsUsedAndDelete(token);
    log.info("Password reset successfully for account with email: {}", token.getEmail());
  }

  private void validatePassword(PasswordResetRequestDTO command) {
    if (command.password() == null || command.password().isBlank()) {
      throw new InvalidPasswordException(INVALID_PASSWORD_MSG);
    }
  }

  private PasswordResetToken validateAndGetToken(PasswordResetRequestDTO command) {
    String tokenStr = command.token();
    if (tokenStr == null || tokenStr.isBlank()) {
      throw new InvalidTokenException("Token must not be null or empty");
    }
    try {
      UUID.fromString(command.token());
    } catch (IllegalArgumentException e) {
      throw new InvalidTokenException("Invalid token format");
    }

    var token = tokenRepository.findByToken(command.token())
        .orElseThrow(() -> new TokenNotFoundException(command.token()));

    if (token.getType() != TokenType.SECONDARY) {
      throw new InvalidTokenException(TOKEN_NOT_SECONDARY_MSG);
    }
    if (token.isUsed()) {
      throw new InvalidTokenException(TOKEN_USED_MSG);
    }
    if (token.isExpired(clock.instant())) {
      throw new InvalidTokenException(TOKEN_EXPIRED_MSG);
    }
    return token;
  }

  private void updateAccountPassword(PasswordResetToken token, PasswordResetRequestDTO command) {
    var account = accountRepository.findAccountByEmail(token.getEmail())
        .orElseThrow(() -> new AccountNotFoundException(token.getEmail()));
    var encodedPassword = passwordHashingPort.hash(command.password());
    var updatedAccount = account.withPassword(encodedPassword);
    accountRepository.updatePassword(updatedAccount.id(), updatedAccount.password());
  }

  private void markTokenAsUsedAndDelete(PasswordResetToken token) {
    var usedToken = token.markAsUsed();
    tokenRepository.save(usedToken);
    tokenRepository.deleteAllByStatus(usedToken.getStatus());
  }
}
