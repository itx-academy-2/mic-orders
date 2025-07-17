package com.academy.orders.domain.passwordreset.usecase;

import com.academy.orders.domain.passwordreset.dto.PasswordResetCommand;

public interface ResetPasswordUseCase {
  void resetPassword(PasswordResetCommand command);
}
