package com.academy.orders.application.passwordreset.usecase;

import com.academy.orders.domain.passwordreset.dto.PasswordResetEmailCommand;
import com.academy.orders.domain.passwordreset.usecase.ProcessPasswordResetUseCase;
import com.academy.orders.domain.passwordreset.usecase.SendPasswordResetEmailUseCase;
import com.academy.orders.domain.ratelimit.usecase.RequestPasswordResetUseCase;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link ProcessPasswordResetUseCase} that handles the logic for rate limit checking and sending password reset emails.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessPasswordResetUseCaseImpl implements ProcessPasswordResetUseCase {
  private final RequestPasswordResetUseCase requestPasswordResetUseCase;
  private final SendPasswordResetEmailUseCase sendPasswordResetEmailUseCase;
  private final Validator validator;

  /**
   * Processes a password reset request by checking rate limits and sending a reset email if allowed.
   *
   * @param email the email address to send the reset link to
   * @param clientIp the IP address of the client initiating the request
   */
  @Override
  @Transactional
  public void processPasswordReset(String email, String clientIp) {
    var command = new PasswordResetEmailCommand(email, clientIp);
    var violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }

    var rateLimitResult = requestPasswordResetUseCase.requestPasswordReset(command);
    if (rateLimitResult.isAllowed()) {
      sendPasswordResetEmailUseCase.sendResetEmail(email);
      log.info("Password reset email sent to {}", email);
    } else {
      log.info("Password reset rate limit exceeded for email {}", email);
    }
  }
}
