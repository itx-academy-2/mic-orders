package com.academy.orders.domain.passwordreset.usecase;

@FunctionalInterface
public interface PasswordHashingPort {
  String hash(String password);
}
