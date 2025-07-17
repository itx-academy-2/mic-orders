package com.academy.orders.domain.passwordreset.usecase;

public interface GenerateSecondaryTokenUseCase {
  String generateSecondaryToken(String primaryToken);
}
