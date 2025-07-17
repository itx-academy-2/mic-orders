package com.academy.orders.domain.passwordreset.usecase;

import com.academy.orders.domain.passwordreset.dto.PasswordResetCommand;

public interface ResetPasswordUseCase {
  /**
 * Resets a user's password based on the provided password reset command.
 *
 * @param command the command containing information required to perform the password reset
 */
void resetPassword(PasswordResetCommand command);
}
