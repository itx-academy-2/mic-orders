package com.academy.orders.infrastructure.account.factory;

import com.academy.orders.domain.account.factory.PasswordResetTokenFactory;
import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenStatus;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import java.time.Clock;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("primaryTokenFactory")
public class PrimaryTokenFactory implements PasswordResetTokenFactory {
  private final Clock clock;

  @Override
  public PasswordResetToken createToken(Long accountId, String email) {
    var now = OffsetDateTime.now(clock);
    return PasswordResetToken.builder()
        .token(UUID.randomUUID().toString())
        .accountId(accountId)
        .email(email)
        .type(TokenType.PRIMARY)
        .status(TokenStatus.ACTIVE)
        .createdAt(now)
        .expiresAt(now.plusMinutes(10))
        .build();
  }
}
