package com.academy.orders.domain.passwordreset.usecase;

public interface CreateTokenUseCase {
  /**
 * Generates a primary token associated with the specified email address.
 *
 * @param email the email address for which to create the primary token
 * @return a newly generated primary token as a string
 */
String createPrimaryToken(String email);
}
