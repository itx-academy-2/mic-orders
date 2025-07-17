package com.academy.orders.domain.passwordreset.usecase;

public interface SendPasswordResetEmailUseCase {
  /**
 * Initiates the process of sending a password reset email to the specified address.
 *
 * @param email the email address to which the password reset instructions will be sent
 */
void sendResetEmail(String email);
}
