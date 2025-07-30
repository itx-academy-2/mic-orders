package com.academy.orders.domain.passwordreset.usecase;

import com.academy.orders.domain.passwordreset.dto.PasswordResetCommand;

/**
 * Use case interface for completing the password reset process.
 * <p>
 * Handles validation of the provided token and securely updates the user's password.
 */
public interface ResetPasswordUseCase {

  /**
   * Resets the user's password using the provided command.
   * <p>
   * This method validates the token included in the command, hashes the new password,
   * stores it securely, and marks the token as used.
   *
   * @param command the password reset command containing the token and new password
   */
  void resetPassword(PasswordResetCommand command);
}
