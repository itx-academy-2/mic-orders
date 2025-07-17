package com.academy.orders.domain.passwordreset.usecase;

public interface GenerateSecondaryTokenUseCase {
  /**
 * Generates a secondary token derived from the provided primary token.
 *
 * @param primaryToken the primary token to base the secondary token on
 * @return a newly generated secondary token
 */
String generateSecondaryToken(String primaryToken);
}
