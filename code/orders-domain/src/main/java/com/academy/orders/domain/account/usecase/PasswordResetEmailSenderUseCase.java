package com.academy.orders.domain.account.usecase;

/**
 * Use case interface for sending password reset emails.
 * <p>
 * This abstraction encapsulates the logic for delivering password reset links
 * or tokens to the user's email address.
 */
public interface PasswordResetEmailSenderUseCase {

  /**
   * Sends a password reset email to the specified recipient.
   *
   * @param email the email address of the recipient
   * @param token the password reset token to be included in the email
   */
  void send(String email, String token);
}
