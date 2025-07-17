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

  public ValidateTokenUseCaseImpl(
      PasswordResetTokenRepository tokenRepository,
      @Qualifier("secondaryTokenFactory") PasswordResetTokenFactory tokenFactory,
      Clock clock) {
    this.tokenRepository = tokenRepository;
    this.tokenFactory = tokenFactory;
    this.clock = clock;
  }

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

  private PasswordResetToken findAndValidateToken(String token) {
    var primaryToken = tokenRepository.findByToken(token)
        .orElseThrow(() -> new TokenNotFoundException(token));

    validateTokenState(primaryToken);
    return primaryToken;
  }

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

  private boolean isTokenExpired(PasswordResetToken token) {
    return token.getExpiresAt() == null || token.isExpired(clock.instant());
  }

  private PasswordResetToken createSecondaryToken(PasswordResetToken primaryToken) {
    var secondaryToken = tokenFactory.createToken(
        primaryToken.getAccountId(),
        primaryToken.getEmail());

    tokenRepository.save(secondaryToken);
    return secondaryToken;
  }

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
