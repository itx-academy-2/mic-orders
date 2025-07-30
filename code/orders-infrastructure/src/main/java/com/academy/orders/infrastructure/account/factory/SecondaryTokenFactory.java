package com.academy.orders.infrastructure.account.factory;

import com.academy.orders.domain.account.factory.PasswordResetTokenFactoryUseCase;
import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenStatus;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component("secondaryTokenFactory")
public class SecondaryTokenFactory implements PasswordResetTokenFactoryUseCase {
  private final Clock clock;

  @Override
  public PasswordResetToken createToken(Long accountId, String email) {
    var now = OffsetDateTime.now(clock);
    log.info("SecondaryTokenFactory: createToken called for accountId={}, email={}", accountId, email);
    return PasswordResetToken.builder()
        .token(UUID.randomUUID().toString())
        .accountId(accountId)
        .email(email)
        .type(TokenType.SECONDARY)
        .status(TokenStatus.ACTIVE)
        .createdAt(now)
        .expiresAt(now.plusMinutes(10))
        .build();
  }
}
