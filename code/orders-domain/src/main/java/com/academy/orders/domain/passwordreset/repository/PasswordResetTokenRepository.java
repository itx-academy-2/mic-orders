package com.academy.orders.domain.passwordreset.repository;

import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenStatus;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository {
  PasswordResetToken save(PasswordResetToken token);

  Optional<PasswordResetToken> findByToken(String token);

  Optional<PasswordResetToken> findLatestPrimaryTokenByAccountId(Long accountId);

  List<PasswordResetToken> findByAccountIdAndTypeAndStatus(Long id, TokenType tokenType, TokenStatus tokenStatus);

  void deleteAllByStatus(TokenStatus status);
}
