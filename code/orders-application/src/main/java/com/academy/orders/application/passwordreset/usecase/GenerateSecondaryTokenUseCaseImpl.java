package com.academy.orders.application.passwordreset.usecase;

import com.academy.orders.domain.account.factory.PasswordResetTokenFactory;
import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import com.academy.orders.domain.passwordreset.exception.InvalidTokenException;
import com.academy.orders.domain.passwordreset.exception.TokenNotFoundException;
import com.academy.orders.domain.passwordreset.repository.PasswordResetTokenRepository;
import com.academy.orders.domain.passwordreset.usecase.GenerateSecondaryTokenUseCase;
import java.time.Clock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class GenerateSecondaryTokenUseCaseImpl implements GenerateSecondaryTokenUseCase {
  static final String NULL_TOKEN_MSG = "Token value cannot be null";
  static final String BLANK_TOKEN_MSG = "Token value cannot be empty or blank";
  static final String NOT_PRIMARY_MSG = "Token is not primary";
  static final String USED_TOKEN_MSG = "Token has already been used";
  static final String EXPIRED_TOKEN_MSG = "Token has expired";
  static final String NULL_SECONDARY_TOKEN_MSG = "Secondary token value cannot be null";

  private final PasswordResetTokenRepository tokenRepository;
  private final PasswordResetTokenFactory tokenFactory;
  private final Clock clock;

  public GenerateSecondaryTokenUseCaseImpl(
      PasswordResetTokenRepository tokenRepository,
      @Qualifier("secondaryTokenFactory") PasswordResetTokenFactory tokenFactory,
      Clock clock) {
    this.tokenRepository = tokenRepository;
    this.tokenFactory = tokenFactory;
    this.clock = clock;
  }

  @Override
  public String generateSecondaryToken(String primaryTokenValue) {
    validateTokenValue(primaryTokenValue);
    var primaryToken = validateAndGetPrimaryToken(primaryTokenValue);
    var secondaryToken = createAndSaveSecondaryToken(primaryToken);

    log.info("Secondary token generated for primary token: {}", primaryTokenValue);
    return secondaryToken.getToken();
  }

  private void validateTokenValue(String tokenValue) {
    if (tokenValue == null) {
      throw new InvalidTokenException(NULL_TOKEN_MSG);
    }
    if (tokenValue.isBlank()) {
      throw new InvalidTokenException(BLANK_TOKEN_MSG);
    }
  }

  private PasswordResetToken validateAndGetPrimaryToken(String tokenValue) {
    var token = tokenRepository.findByToken(tokenValue)
        .orElseThrow(() -> new TokenNotFoundException(tokenValue));

    if (token.getType() != TokenType.PRIMARY) {
      log.warn("Invalid token type for token: {}. Expected primary, found {}", tokenValue, token.getType());
      throw new InvalidTokenException(NOT_PRIMARY_MSG);
    }
    if (token.isUsed()) {
      log.warn("Token already used: {}", tokenValue);
      throw new InvalidTokenException(USED_TOKEN_MSG);
    }
    if (token.isExpired(clock.instant())) {
      log.warn("Token expired: {}", tokenValue);
      throw new InvalidTokenException(EXPIRED_TOKEN_MSG);
    }
    return token;
  }

  private PasswordResetToken createAndSaveSecondaryToken(PasswordResetToken primaryToken) {
    log.info("Primary token status before markAsUsed: {}", primaryToken.getStatus());

    var usedPrimaryToken = primaryToken.markAsUsed();
    log.info("Primary token status after markAsUsed: {}", usedPrimaryToken.getStatus());

    tokenRepository.save(usedPrimaryToken);
    log.info("Primary token {} marked as USED", usedPrimaryToken.getToken());
    var secondaryToken = tokenFactory.createToken(
        primaryToken.getAccountId(),
        primaryToken.getEmail());

    if (secondaryToken.getToken() == null) {
      log.error("Null secondary token generated for primary token with email: {}", primaryToken.getEmail());
      throw new InvalidTokenException(NULL_SECONDARY_TOKEN_MSG);
    }
    tokenRepository.save(secondaryToken);
    return secondaryToken;
  }
}
