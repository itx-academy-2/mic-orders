package com.academy.orders.infrastructure.passwordreset.repository;

import com.academy.orders.domain.passwordreset.entity.enumerated.TokenStatus;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import com.academy.orders.infrastructure.passwordreset.entity.PasswordResetTokenEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PasswordResetTokenJpaAdaptor extends JpaRepository<PasswordResetTokenEntity, Long> {

  /**
   * Finds a password reset token entity by its token string, status, and checks that it has not expired (expiresAt is after the provided
   * time).
   *
   * @param token the token string (UUID)
   * @param status the status of the token (e.g., ACTIVE)
   * @param now the current time to check expiry against
   * @return Optional containing the found PasswordResetTokenEntity, or empty if none found
   */
  Optional<PasswordResetTokenEntity> findByTokenAndStatusAndExpiresAtAfter(String token, TokenStatus status, OffsetDateTime now);

  /**
   * Finds the most recently created password reset token entity for the given account, token type, and status.
   *
   * @param accountId the ID of the account (user)
   * @param type the type of token (e.g., PRIMARY, SECONDARY)
   * @param status the status of the token (e.g., ACTIVE)
   * @return Optional containing the most recent PasswordResetTokenEntity, or empty if none found
   */
  Optional<PasswordResetTokenEntity> findFirstByAccountIdAndTypeAndStatusOrderByCreatedAtDesc(
      Long accountId, TokenType type, TokenStatus status);

  /**
   * Retrieves all password reset tokens for a given account, token type, and status.
   *
   * @param accountId the ID of the account (user)
   * @param type the type of token (e.g., PRIMARY, SECONDARY)
   * @param status the status of the token (e.g., ACTIVE)
   * @return List of PasswordResetTokenEntity matching the criteria
   */
  List<PasswordResetTokenEntity> findByAccountIdAndTypeAndStatus(Long accountId, TokenType type, TokenStatus status);

  /**
   * Deletes all password reset token entities having the specified status. This operation is modifying and transactional.
   *
   * @param status the status of tokens to delete (e.g., EXPIRED, USED)
   */
  @Modifying
  @Transactional
  @Query("DELETE FROM PasswordResetTokenEntity t WHERE t.status = :status")
  void deleteAllByStatus(TokenStatus status);
}
