package com.academy.orders.application.ratelimit.usecase;

import com.academy.orders.domain.passwordreset.dto.PasswordResetEmailCommand;
import com.academy.orders.domain.ratelimit.dto.RateLimitResult;
import com.academy.orders.domain.ratelimit.usecase.CheckRateLimitUseCase;
import com.academy.orders.domain.ratelimit.usecase.ClientIpExtractorUseCase;
import com.academy.orders.domain.ratelimit.usecase.RequestPasswordResetUseCase;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestPasswordResetUseCaseImpl implements RequestPasswordResetUseCase {
  private final CheckRateLimitUseCase rateLimitUseCase;
  private final ClientIpExtractorUseCase clientIpExtractorUseCase;

  @Override
  @Transactional
  public RateLimitResult requestPasswordReset(PasswordResetEmailCommand command) {
    var clientIp = clientIpExtractorUseCase.extractClientIp();
    log.info("Processing password reset request for email hash: {}", hashEmail(command.email()));
    return rateLimitUseCase.checkRateLimit(clientIp, command.email());
  }

  private String hashEmail(String email) {
    return DigestUtils.sha256Hex(
        Objects.requireNonNull(email, "Email must not be null")
            .toLowerCase());
  }
}
