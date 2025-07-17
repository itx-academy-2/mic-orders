package com.academy.orders.domain.account.usecase;

public interface PasswordResetEmailSender {
  void send(String email, String token);
}
