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

  @Override
  @Transactional
  public PasswordResetToken save(PasswordResetToken token) {
    PasswordResetTokenEntity entity = mapper.toEntity(token);
    PasswordResetTokenEntity saved = jpaAdaptor.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<PasswordResetToken> findByToken(String token) {
    return jpaAdaptor.findByTokenAndStatusAndExpiresAtAfter(token, TokenStatus.ACTIVE, OffsetDateTime.now())
        .map(mapper::toDomain);
  }

  @Override
  public Optional<PasswordResetToken> findLatestPrimaryTokenByAccountId(Long accountId) {
    return jpaAdaptor.findFirstByAccountIdAndTypeAndStatusOrderByCreatedAtDesc(
        accountId, TokenType.PRIMARY, TokenStatus.ACTIVE)
        .map(mapper::toDomain);
  }

  @Override
  public List<PasswordResetToken> findByAccountIdAndTypeAndStatus(Long id, TokenType tokenType, TokenStatus tokenStatus) {
    return jpaAdaptor.findByAccountIdAndTypeAndStatus(id, tokenType, tokenStatus);
  }

  @Override
  @Transactional
  public void deleteAllByStatus(TokenStatus status) {
    jpaAdaptor.deleteAllByStatus(status);
  }
}
