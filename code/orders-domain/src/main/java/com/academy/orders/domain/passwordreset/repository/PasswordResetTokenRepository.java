package com.academy.orders.domain.passwordreset.repository;

import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenStatus;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository {
  /**
 * Persists the given PasswordResetToken entity and returns the saved instance.
 *
 * @param token the PasswordResetToken entity to be saved
 * @return the persisted PasswordResetToken entity
 */
PasswordResetToken save(PasswordResetToken token);

  /**
 * Retrieves a password reset token entity matching the specified token string.
 *
 * @param token the token string to search for
 * @return an {@code Optional} containing the matching {@code PasswordResetToken} if found, or empty if not found
 */
Optional<PasswordResetToken> findByToken(String token);

  /**
 * Retrieves the most recent primary password reset token associated with the specified account ID.
 *
 * @param accountId the unique identifier of the account
 * @return an {@code Optional} containing the latest primary {@code PasswordResetToken} if found, or empty if none exists
 */
Optional<PasswordResetToken> findLatestPrimaryTokenByAccountId(Long accountId);

  /**
 * Retrieves a list of password reset tokens for the specified account ID, token type, and token status.
 *
 * @param id the account ID to filter tokens by
 * @param tokenType the type of token to filter by
 * @param tokenStatus the status of the tokens to filter by
 * @return a list of matching PasswordResetToken entities, or an empty list if none are found
 */
List<PasswordResetToken> findByAccountIdAndTypeAndStatus(Long id, TokenType tokenType, TokenStatus tokenStatus);

  /**
 * Deletes all password reset tokens that have the specified status.
 *
 * @param status the status of tokens to delete
 */
void deleteAllByStatus(TokenStatus status);
}
