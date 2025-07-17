package com.academy.orders.domain.account.factory;

import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;

public interface PasswordResetTokenFactory {
  /**
 * Creates a password reset token for the specified account and email address.
 *
 * @param accountId the unique identifier of the account
 * @param email the email address associated with the account
 * @return a new PasswordResetToken for the given account and email
 */
PasswordResetToken createToken(Long accountId, String email);
}
