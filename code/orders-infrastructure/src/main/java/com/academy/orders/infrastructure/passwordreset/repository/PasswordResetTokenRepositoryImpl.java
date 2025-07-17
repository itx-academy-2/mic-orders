package com.academy.orders.infrastructure.passwordreset.repository;

import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenStatus;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import com.academy.orders.domain.passwordreset.repository.PasswordResetTokenRepository;
import com.academy.orders.infrastructure.passwordreset.PasswordResetTokenMapper;
import com.academy.orders.infrastructure.passwordreset.entity.PasswordResetTokenEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepository {
  private final PasswordResetTokenJpaAdaptor jpaAdaptor;

  private final PasswordResetTokenMapper mapper;

  /**
   * Persists a password reset token and returns the saved domain object.
   *
   * @param token the password reset token to be saved
   * @return the saved password reset token as a domain object
   */
  @Override
  @Transactional
  public PasswordResetToken save(PasswordResetToken token) {
    PasswordResetTokenEntity entity = mapper.toEntity(token);
    PasswordResetTokenEntity saved = jpaAdaptor.save(entity);
    return mapper.toDomain(saved);
  }

  /**
   * Retrieves an active, unexpired password reset token by its token string.
   *
   * @param token the token string to search for
   * @return an {@code Optional} containing the matching {@code PasswordResetToken} if found and valid; otherwise, an empty {@code Optional}
   */
  @Override
  public Optional<PasswordResetToken> findByToken(String token) {
    return jpaAdaptor.findByTokenAndStatusAndExpiresAtAfter(token, TokenStatus.ACTIVE, OffsetDateTime.now())
        .map(mapper::toDomain);
  }

  /**
   * Retrieves the most recently created active primary password reset token for the specified account ID.
   *
   * @param accountId the ID of the account for which to find the latest primary token
   * @return an {@code Optional} containing the latest active primary {@code PasswordResetToken} if found, or empty if none exists
   */
  @Override
  public Optional<PasswordResetToken> findLatestPrimaryTokenByAccountId(Long accountId) {
    return jpaAdaptor.findFirstByAccountIdAndTypeAndStatusOrderByCreatedAtDesc(
        accountId, TokenType.PRIMARY, TokenStatus.ACTIVE)
        .map(mapper::toDomain);
  }

  /**
   * Retrieves all password reset tokens for the specified account ID, token type, and token status.
   *
   * @param id the account ID to filter tokens by
   * @param tokenType the type of token to filter by
   * @param tokenStatus the status of tokens to filter by
   * @return a list of matching password reset tokens
   */
  @Override
  public List<PasswordResetToken> findByAccountIdAndTypeAndStatus(Long id, TokenType tokenType, TokenStatus tokenStatus) {
    return jpaAdaptor.findByAccountIdAndTypeAndStatus(id, tokenType, tokenStatus);
  }

  /**
   * Deletes all password reset tokens with the specified status.
   *
   * @param status the status of tokens to delete
   */
  @Override
  @Transactional
  public void deleteAllByStatus(TokenStatus status) {
    jpaAdaptor.deleteAllByStatus(status);
  }
}
