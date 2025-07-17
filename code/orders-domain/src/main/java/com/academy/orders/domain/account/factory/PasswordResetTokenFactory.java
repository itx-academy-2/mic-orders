package com.academy.orders.domain.account.factory;

import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;

public interface PasswordResetTokenFactory {
  PasswordResetToken createToken(Long accountId, String email);
}
