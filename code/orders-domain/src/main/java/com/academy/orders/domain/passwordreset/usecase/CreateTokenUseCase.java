package com.academy.orders.domain.passwordreset.usecase;

/**
 * Use case interface for creating password reset tokens.
 * <p>
 * Provides functionality to generate a primary token for initiating
 * the password reset process.
 */
public interface CreateTokenUseCase {

  /**
   * Creates a primary password reset token for the user identified by the given email address.
   *
   * @param email the email address of the user requesting password reset
   * @return the generated primary token as a string
   */
  String createPrimaryToken(String email);
}
