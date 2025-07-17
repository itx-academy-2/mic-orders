package com.academy.orders.application.passwordreset.usecase;

import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.account.repository.AccountRepository;
import com.academy.orders.domain.passwordreset.dto.PasswordResetCommand;
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

  /**
   * Resets the account password using the provided password reset command.
   *
   * Validates the new password and reset token, updates the account's password, marks the token as used, and removes used tokens. Ensures transactional consistency and throws domain-specific exceptions if validation fails.
   */
  @Override
  public void resetPassword(PasswordResetCommand command) {
    validatePassword(command);
    var token = validateAndGetToken(command);
    updateAccountPassword(token, command);
    markTokenAsUsedAndDelete(token);
    log.info("Password reset successfully for account with email: {}", token.getEmail());
  }

  /**
   * Validates that the password in the given command is not null or blank.
   *
   * @param command the password reset command containing the new password
   * @throws InvalidPasswordException if the password is null or blank
   */
  private void validatePassword(PasswordResetCommand command) {
    if (command.password() == null || command.password().isBlank()) {
      throw new InvalidPasswordException(INVALID_PASSWORD_MSG);
    }
  }

  /**
   * Validates the password reset token from the command and retrieves the corresponding token entity.
   *
   * Checks that the token string is present, correctly formatted as a UUID, exists in the repository, is of type SECONDARY, has not been used, and is not expired. Throws an appropriate exception if any validation fails.
   *
   * @param command the password reset command containing the token to validate
   * @return the valid PasswordResetToken entity
   * @throws InvalidTokenException if the token is missing, improperly formatted, not of type SECONDARY, already used, or expired
   * @throws TokenNotFoundException if the token does not exist in the repository
   */
  private PasswordResetToken validateAndGetToken(PasswordResetCommand command) {
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

  /**
   * Updates the account's password with the new hashed password from the reset command.
   *
   * Retrieves the account associated with the email in the provided token, hashes the new password,
   * and updates the account's password in the repository. Throws {@code AccountNotFoundException} if the account does not exist.
   */
  private void updateAccountPassword(PasswordResetToken token, PasswordResetCommand command) {
    var account = accountRepository.findAccountByEmail(token.getEmail())
        .orElseThrow(() -> new AccountNotFoundException(token.getEmail()));
    var encodedPassword = passwordHashingPort.hash(command.password());
    var updatedAccount = account.withPassword(encodedPassword);
    accountRepository.updatePassword(updatedAccount.id(), updatedAccount.password());
  }

  /**
   * Marks the given password reset token as used, saves the updated token, and deletes all tokens with the used status from the repository.
   *
   * @param token the password reset token to mark as used and clean up
   */
  private void markTokenAsUsedAndDelete(PasswordResetToken token) {
    var usedToken = token.markAsUsed();
    tokenRepository.save(usedToken);
    tokenRepository.deleteAllByStatus(usedToken.getStatus());
  }
}
