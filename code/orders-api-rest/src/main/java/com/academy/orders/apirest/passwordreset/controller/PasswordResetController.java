package com.academy.orders.apirest.passwordreset.controller;

import com.academy.orders.apirest.passwordreset.mapper.PasswordResetMapper;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import com.academy.orders.domain.passwordreset.dto.TokenWrapper;
import com.academy.orders.domain.passwordreset.usecase.ProcessPasswordResetUseCase;
import com.academy.orders.domain.passwordreset.usecase.ResetPasswordUseCase;
import com.academy.orders.domain.passwordreset.usecase.ValidateTokenUseCase;
import com.academy.orders.domain.ratelimit.usecase.ClientIpExtractorUseCase;
import com.academy.orders_api_rest.generated.api.PasswordResetControllerApi;
import com.academy.orders_api_rest.generated.model.PasswordResetEmailRequestDTO;
import com.academy.orders_api_rest.generated.model.PasswordResetRequestDTO;
import com.academy.orders_api_rest.generated.model.PasswordResetSuccessResponseDTO;
import com.academy.orders_api_rest.generated.model.TokenValidResponseDTO;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PasswordResetController implements PasswordResetControllerApi {
  private final ProcessPasswordResetUseCase processPasswordResetUseCase;

  private final ClientIpExtractorUseCase clientIpExtractorUseCase;

  private final ResetPasswordUseCase resetPasswordUseCase;

  private final ValidateTokenUseCase validateTokenUseCase;

  private final PasswordResetMapper mapper;

  @Override
  @RateLimiter(name = "passwordReset")
  @PreAuthorize("permitAll()")
  public ResponseEntity<PasswordResetSuccessResponseDTO> v1PasswordResetPost(
      @Valid PasswordResetEmailRequestDTO passwordResetEmailRequestDTO) {
    log.info("Processing password reset email request");
    var clientIp = clientIpExtractorUseCase.extractClientIp();
    processPasswordResetUseCase.processPasswordReset(passwordResetEmailRequestDTO.getEmail(), clientIp);
    var response = mapper.toSuccessResponseForEmail();
    return ResponseEntity.ok(response);
  }

  @Override
  @PreAuthorize("permitAll()")
  public ResponseEntity<PasswordResetSuccessResponseDTO> v1PasswordResetPut(
      @Valid PasswordResetRequestDTO passwordResetRequestDTO) {
    log.info("Processing password reset request");
    var command = mapper.toCommand(passwordResetRequestDTO);
    resetPasswordUseCase.resetPassword(command);
    var response = mapper.toSuccessResponseForReset(new TokenWrapper(passwordResetRequestDTO.getToken()));
    return ResponseEntity.ok(response);
  }

  @Override
  @PreAuthorize("permitAll()")
  public ResponseEntity<TokenValidResponseDTO> v1PasswordResetTokenGet(UUID token) {
    log.info("Validating password reset token");
    var result = validateTokenUseCase.validatePrimaryToken(token.toString());
    var dto = mapper.toTokenValidResponse(result);
    return ResponseEntity.ok(dto);
  }
}
