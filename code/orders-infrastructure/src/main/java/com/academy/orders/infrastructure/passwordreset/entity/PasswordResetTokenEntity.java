package com.academy.orders.infrastructure.passwordreset.entity;

import com.academy.orders.domain.passwordreset.entity.enumerated.TokenStatus;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.time.OffsetDateTime;
import lombok.*;

@Entity
@Table(name = "password_reset_tokens",
    indexes = {
        @Index(name = "idx_password_reset_token", columnList = "token"),
        @Index(name = "idx_password_reset_account_id", columnList = "account_id"),
        @Index(name = "idx_password_reset_account_type_status_created", columnList = "account_id, type, status, created_at")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
public class PasswordResetTokenEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_reset_token_seq_gen")
  @SequenceGenerator(
      name = "password_reset_token_seq_gen",
      sequenceName = "password_reset_token_seq",
      allocationSize = 1)
  @ToString.Include
  private Long id;

  @Column(name = "token", nullable = false, unique = true, length = 256)
  @ToString.Include
  private String token;

  @Email
  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(name = "expiry", nullable = false)
  private OffsetDateTime expiresAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private TokenStatus status;

  @Column(name = "account_id", nullable = false)
  private Long accountId;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private TokenType type;
}
