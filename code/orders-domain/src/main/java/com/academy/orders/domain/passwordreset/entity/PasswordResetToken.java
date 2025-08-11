package com.academy.orders.domain.passwordreset.entity;

import com.academy.orders.domain.passwordreset.entity.enumerated.TokenStatus;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import java.time.Instant;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class PasswordResetToken {
  Long id;

  String token;

  Long accountId;

  String email;

  TokenType type;

  TokenStatus status;

  OffsetDateTime createdAt;

  OffsetDateTime expiresAt;

  public boolean isUsed() {
    return status == TokenStatus.USED;
  }

  public boolean isExpired(Instant now) {
    if (expiresAt == null) {
      return true;
    }
    return expiresAt.toInstant().isBefore(now);
  }

  public boolean isActive() {
    return status == TokenStatus.ACTIVE;
  }

  public PasswordResetToken markAsUsed() {
    return this.toBuilder().status(TokenStatus.USED).build();
  }
}
