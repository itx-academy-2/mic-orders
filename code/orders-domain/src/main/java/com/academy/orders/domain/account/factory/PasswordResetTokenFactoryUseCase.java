package com.academy.orders.domain.account.factory;

import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;

/**
 * Factory interface for creating instances of {@link PasswordResetToken}. <p> This abstraction allows for centralized construction of
 * password reset tokens, ensuring proper initialization (e.g., setting token value, expiration, and status).
 */
public interface PasswordResetTokenFactoryUseCase {

  /**
   * Creates a new {@link PasswordResetToken} for the specified account and email.
   *
   * @param accountId the ID of the account requesting password reset
   * @param email the email address associated with the account
   * @return a newly created {@link PasswordResetToken} instance
   */
  PasswordResetToken createToken(Long accountId, String email);
}
