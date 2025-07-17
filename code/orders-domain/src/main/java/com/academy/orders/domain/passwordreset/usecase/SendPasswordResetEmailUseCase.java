package com.academy.orders.domain.passwordreset.usecase;

public interface SendPasswordResetEmailUseCase {
  void sendResetEmail(String email);
}
