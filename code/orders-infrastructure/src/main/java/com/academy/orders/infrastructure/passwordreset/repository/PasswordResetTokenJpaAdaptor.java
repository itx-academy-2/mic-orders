package com.academy.orders.infrastructure.passwordreset.repository;

import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
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
 * Retrieves a password reset token entity matching the given token string and status, ensuring it has not expired as of the specified time.
 *
 * @param token the unique token string to search for
 * @param status the required token status
 * @param now the time to compare against the token's expiration
 * @return an Optional containing the matching PasswordResetTokenEntity if found and not expired; otherwise, an empty Optional
 */
  Optional<PasswordResetTokenEntity> findByTokenAndStatusAndExpiresAtAfter(String token, TokenStatus status, OffsetDateTime now);

  /**
       * Retrieves the most recently created password reset token entity for a specified account, token type, and status.
       *
       * @param accountId the unique identifier of the account
       * @param type the type of the password reset token
       * @param status the status of the password reset token
       * @return an Optional containing the latest PasswordResetTokenEntity matching the criteria, or empty if none exist
       */
  Optional<PasswordResetTokenEntity> findFirstByAccountIdAndTypeAndStatusOrderByCreatedAtDesc(
      Long accountId, TokenType type, TokenStatus status);

  /**
 * Finds all password reset token entities matching the specified account ID, token type, and status.
 *
 * @param accountId the unique identifier of the account
 * @param type the type of password reset token
 * @param status the status of the token
 * @return a list of password reset token entities that match the given criteria
 */
  List<PasswordResetToken> findByAccountIdAndTypeAndStatus(Long accountId, TokenType type, TokenStatus status);

  /**
   * Deletes all password reset token entities with the given status.
   *
   * This operation is performed within a transaction and modifies the database directly.
   *
   * @param status the status of the tokens to be deleted
   */
  @Modifying
  @Transactional
  @Query("DELETE FROM PasswordResetTokenEntity t WHERE t.status = :status")
  void deleteAllByStatus(TokenStatus status);
}
