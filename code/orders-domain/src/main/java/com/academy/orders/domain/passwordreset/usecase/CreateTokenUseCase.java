package com.academy.orders.domain.passwordreset.usecase;

public interface CreateTokenUseCase {
  String createPrimaryToken(String email);
}
