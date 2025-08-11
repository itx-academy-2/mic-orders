package com.academy.orders.domain.passwordreset.usecase;

/**
 * Use case interface for processing a password reset request.
 *
 * <p>Handles the workflow of validating rate limits and sending password reset emails.</p>
 */
public interface ProcessPasswordResetUseCase {
  /**
   * Processes a password reset request for the given email and client IP address.
   *
   * @param email the email address to reset the password for
   * @param clientIp the IP address of the client making the request
   */
  void processPasswordReset(String email, String clientIp);
}
