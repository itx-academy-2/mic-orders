package com.academy.orders.domain.account.usecase;

public interface PasswordResetEmailSender {
  /**
 * Sends a password reset email to the specified address using the provided token.
 *
 * @param email the recipient's email address
 * @param token the password reset token to include in the email
 */
void send(String email, String token);
}
