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

  /**
   * Checks if the token has been marked as used.
   *
   * @return true if the token status is USED; false otherwise
   */
  public boolean isUsed() {
    return status == TokenStatus.USED;
  }

  /**
   * Determines whether the token has expired based on the provided instant.
   *
   * @param now the current instant to compare against the token's expiration time
   * @return true if the token has expired or if the expiration time is not set; false otherwise
   */
  public boolean isExpired(Instant now) {
    if (expiresAt == null) {
      return true;
    }
    return expiresAt.toInstant().isBefore(now);
  }

  /**
   * Checks if the token is currently active.
   *
   * @return true if the token status is ACTIVE; false otherwise
   */
  public boolean isActive() {
    return status == TokenStatus.ACTIVE;
  }

  /**
   * Returns a new instance of this token with its status set to {@code USED}.
   *
   * @return a copy of this token marked as used
   */
  public PasswordResetToken markAsUsed() {
    return this.toBuilder().status(TokenStatus.USED).build();
  }
}
