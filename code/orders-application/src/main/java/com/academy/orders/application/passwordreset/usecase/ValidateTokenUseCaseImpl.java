package com.academy.orders.application.passwordreset.usecase;

import com.academy.orders.domain.account.factory.PasswordResetTokenFactory;
import com.academy.orders.domain.passwordreset.dto.TokenValidationResult;
import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import com.academy.orders.domain.passwordreset.exception.InvalidTokenException;
import com.academy.orders.domain.passwordreset.exception.TokenNotFoundException;
import com.academy.orders.domain.passwordreset.repository.PasswordResetTokenRepository;
import com.academy.orders.domain.passwordreset.usecase.ValidateTokenUseCase;
import java.time.Clock;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ValidateTokenUseCaseImpl implements ValidateTokenUseCase {
  private final PasswordResetTokenRepository tokenRepository;

  private final PasswordResetTokenFactory tokenFactory;

  private final Clock clock;

  /**
   * Constructs a new instance of ValidateTokenUseCaseImpl with the specified token repository, secondary token factory, and clock.
   */
  public ValidateTokenUseCaseImpl(
      PasswordResetTokenRepository tokenRepository,
      @Qualifier("secondaryTokenFactory") PasswordResetTokenFactory tokenFactory,
      Clock clock) {
    this.tokenRepository = tokenRepository;
    this.tokenFactory = tokenFactory;
    this.clock = clock;
  }

  /**
   * Validates a primary password reset token, marks it as used, and issues a secondary token.
   *
   * If the provided token is valid, unused, unexpired, and of type PRIMARY, it is marked as used and persisted.
   * A new secondary token is then created and returned as part of the validation result.
   *
   * @param token the primary password reset token string to validate
   * @return a {@link TokenValidationResult} containing details of the validation and the newly created secondary token
   * @throws InvalidTokenException if the token format is invalid or the token is not eligible for use
   * @throws TokenNotFoundException if the token does not exist
   */
  @Override
  @Transactional
  public TokenValidationResult validatePrimaryToken(String token) {
    validateTokenFormat(token);
    var primaryToken = findAndValidateToken(token);
    var usedPrimaryToken = primaryToken.markAsUsed();
    tokenRepository.save(usedPrimaryToken);
    var secondaryToken = createSecondaryToken(usedPrimaryToken);
    return buildValidationResult(usedPrimaryToken, secondaryToken);
  }

  /**
   * Validates that the provided token string is a non-null, non-blank, valid UUID.
   *
   * @param token the token string to validate
   * @throws InvalidTokenException if the token is null, blank, or not a valid UUID
   */
  private void validateTokenFormat(String token) {
    if (token == null || token.isBlank()) {
      throw new InvalidTokenException("Token must not be null or empty.");
    }
    try {
      UUID.fromString(token);
    } catch (IllegalArgumentException e) {
      throw new InvalidTokenException("Token format is invalid.");
    }
  }

  /**
   * Retrieves a password reset token by its string value and validates its state.
   *
   * @param token the token string to look up and validate
   * @return the validated PasswordResetToken entity
   * @throws TokenNotFoundException if no token is found for the provided string
   * @throws InvalidTokenException if the token is in an invalid state
   */
  private PasswordResetToken findAndValidateToken(String token) {
    var primaryToken = tokenRepository.findByToken(token)
        .orElseThrow(() -> new TokenNotFoundException(token));

    validateTokenState(primaryToken);
    return primaryToken;
  }

  /**
   * Validates that the given password reset token is a valid, unused, non-expired primary token.
   *
   * @param token the password reset token to validate
   * @throws InvalidTokenException if the token status is null, the type is not PRIMARY, the token is already used, or the token is expired
   */
  private void validateTokenState(PasswordResetToken token) {
    if (token.getStatus() == null) {
      throw new InvalidTokenException("Token status is invalid.");
    }
    if (token.getType() != TokenType.PRIMARY) {
      throw new InvalidTokenException("Token type is invalid.");
    }
    if (token.isUsed()) {
      throw new InvalidTokenException("Token already used.");
    }
    if (isTokenExpired(token)) {
      throw new InvalidTokenException("Token expired.");
    }
  }

  /**
   * Determines whether the given password reset token is expired.
   *
   * @param token the password reset token to check
   * @return true if the token has no expiration date or is expired relative to the current time; false otherwise
   */
  private boolean isTokenExpired(PasswordResetToken token) {
    return token.getExpiresAt() == null || token.isExpired(clock.instant());
  }

  /**
   * Creates and persists a secondary password reset token for the account and email associated with the given primary token.
   *
   * @param primaryToken the primary password reset token to base the secondary token on
   * @return the newly created secondary password reset token
   */
  private PasswordResetToken createSecondaryToken(PasswordResetToken primaryToken) {
    var secondaryToken = tokenFactory.createToken(
        primaryToken.getAccountId(),
        primaryToken.getEmail());

    tokenRepository.save(secondaryToken);
    return secondaryToken;
  }

  /**
   * Constructs a {@link TokenValidationResult} representing a successful primary token validation and secondary token issuance.
   *
   * @param primaryToken    the validated primary password reset token
   * @param secondaryToken  the newly created secondary password reset token
   * @return a {@code TokenValidationResult} containing validation status, token identifiers, token type, secondary token expiry, and the current timestamp
   */
  private TokenValidationResult buildValidationResult(
      PasswordResetToken primaryToken,
      PasswordResetToken secondaryToken) {
    var currentTime = clock.instant();
    var primaryTokenId = UUID.fromString(primaryToken.getToken());
    var secondaryTokenId = UUID.fromString(secondaryToken.getToken());
    var secondaryTokenExpiry = secondaryToken.getExpiresAt().toInstant();

    return new TokenValidationResult(
        true,
        primaryTokenId,
        "password_reset",
        secondaryTokenExpiry,
        secondaryTokenId,
        currentTime);
  }
}
