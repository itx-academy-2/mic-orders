package com.academy.orders.domain.passwordreset.usecase;

/**
 * Use case interface for initiating the password reset process. <p> Responsible for validating the provided email and sending a password
 * reset email containing a primary token if the account exists.
 */
public interface SendPasswordResetEmailUseCase {

  /**
   * Sends a password reset email to the user with the given email address. <p> If an account with the provided email exists, a primary
   * reset token is generated and included in the email. If the account does not exist, the operation completes silently.
   *
   * @param email the email address associated with the user's account
   */
  void sendResetEmail(String email);
}
