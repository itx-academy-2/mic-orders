package com.academy.orders.application.passwordreset.usecase;

import com.academy.orders.domain.account.entity.Account;
import com.academy.orders.domain.account.factory.PasswordResetTokenFactory;
import com.academy.orders.domain.account.repository.AccountRepository;
import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenStatus;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import com.academy.orders.domain.passwordreset.exception.InvalidEmailException;
import com.academy.orders.domain.passwordreset.exception.InvalidTokenException;
import com.academy.orders.domain.passwordreset.repository.PasswordResetTokenRepository;
import com.academy.orders.domain.passwordreset.usecase.CreateTokenUseCase;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class CreateTokenUseCaseImpl implements CreateTokenUseCase {
  static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

  static final String NULL_EMAIL_MSG = "Email cannot be null";

  static final String BLANK_EMAIL_MSG = "Email cannot be empty or blank";

  static final String INVALID_EMAIL_MSG = "Invalid email format";

  private final AccountRepository accountRepository;

  private final PasswordResetTokenRepository tokenRepository;

  private final PasswordResetTokenFactory tokenFactory;

  /**
   * Constructs a new instance of CreateTokenUseCaseImpl with the specified repositories and token factory.
   *
   * @param accountRepository the repository for accessing account data
   * @param tokenRepository the repository for managing password reset tokens
   * @param tokenFactory the factory for creating primary password reset tokens
   */
  public CreateTokenUseCaseImpl(
      AccountRepository accountRepository,
      PasswordResetTokenRepository tokenRepository,
      @Qualifier("primaryTokenFactory") PasswordResetTokenFactory tokenFactory) {
    this.accountRepository = accountRepository;
    this.tokenRepository = tokenRepository;
    this.tokenFactory = tokenFactory;
  }

  /**
   * Creates a primary password reset token for the account associated with the given email address.
   *
   * Validates the email format and, if valid, generates and returns a new primary password reset token.
   * If no account exists for the provided email, returns a random UUID string.
   *
   * @param email the email address for which to create the password reset token
   * @return the generated password reset token string
   * @throws InvalidEmailException if the email is null, blank, or invalid in format
   * @throws InvalidTokenException if a token is created with a null email
   */
  @Override
  public String createPrimaryToken(String email) {
    validateEmail(email);
    return generateTokenForEmail(email);
  }

  /**
   * Validates the provided email address, ensuring it is not null, not blank, and matches the required email format.
   *
   * @param email the email address to validate
   * @throws InvalidEmailException if the email is null, blank, or does not match the expected format
   */
  private void validateEmail(String email) {
    if (email == null) {
      throw new InvalidEmailException(NULL_EMAIL_MSG);
    }
    if (email.isBlank()) {
      throw new InvalidEmailException(BLANK_EMAIL_MSG);
    }
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new InvalidEmailException(INVALID_EMAIL_MSG);
    }
  }

  /**
   * Generates a new primary password reset token for the specified email address.
   *
   * If an account with the given email exists, marks all existing active primary tokens for that account as used,
   * creates and saves a new primary token, and returns the token string. If no account is found, returns a random UUID string.
   *
   * @param email the email address for which to generate the token
   * @return the generated token string, or a random UUID if the account does not exist
   * @throws InvalidTokenException if the created token has a null email
   */
  private String generateTokenForEmail(String email) {
    Optional<Account> account = accountRepository.findAccountByEmail(email);

    if (account.isEmpty()) {
      log.info("No account found for email: {}. Returning random UUID.", email);
      return UUID.randomUUID().toString();
    }

    Account accountEntity = account.get();
    log.info("Creating token for account ID: {}, email: {}", accountEntity.id(), email);

    var activeTokens = tokenRepository.findByAccountIdAndTypeAndStatus(
        accountEntity.id(),
        TokenType.PRIMARY,
        TokenStatus.ACTIVE);

    for (PasswordResetToken oldToken : activeTokens) {
      PasswordResetToken usedToken = oldToken.markAsUsed();
      tokenRepository.save(usedToken);
      log.info("Marked old primary token {} as USED", oldToken.getToken());
    }

    PasswordResetToken token = tokenFactory.createToken(accountEntity.id(), email);

    if (token.getEmail() == null) {
      log.error("Token email is null for email: {}", email);
      throw new InvalidTokenException("Token email cannot be null");
    }

    log.info("Saving primary token for email: {}", email);
    tokenRepository.save(token);
    log.info("Primary token created for email: {}", email);
    return token.getToken();
  }

}
