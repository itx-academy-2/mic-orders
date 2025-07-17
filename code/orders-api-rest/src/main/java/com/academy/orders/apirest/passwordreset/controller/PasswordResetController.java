package com.academy.orders.apirest.passwordreset.controller;

import com.academy.orders.apirest.passwordreset.mapper.PasswordResetMapper;
import com.academy.orders.domain.passwordreset.dto.TokenWrapper;
import com.academy.orders.domain.passwordreset.usecase.ResetPasswordUseCase;
import com.academy.orders.domain.passwordreset.usecase.SendPasswordResetEmailUseCase;
import com.academy.orders.domain.passwordreset.usecase.ValidateTokenUseCase;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class PasswordResetController implements PasswordResetControllerApi {
  private final SendPasswordResetEmailUseCase sendPasswordResetEmailUseCase;

  private final ResetPasswordUseCase resetPasswordUseCase;

  private final ValidateTokenUseCase validateTokenUseCase;

  private final PasswordResetMapper mapper;

  /**
   * Initiates the password reset process by sending a reset email to the specified address.
   *
   * @param passwordResetEmailRequestDTO the request containing the user's email address
   * @return a response entity containing a success response DTO
   */
  @Override
  @PreAuthorize("permitAll()")
  public ResponseEntity<PasswordResetSuccessResponseDTO> v1PasswordResetPost(
      @Valid PasswordResetEmailRequestDTO passwordResetEmailRequestDTO) {
    log.info("Sending password reset email for: {}", passwordResetEmailRequestDTO.getEmail());
    sendPasswordResetEmailUseCase.sendResetEmail(passwordResetEmailRequestDTO.getEmail());
    var response = mapper.toSuccessResponseForEmail();
    return ResponseEntity.ok(response);
  }

  /**
   * Resets the user's password using the provided token and new password details.
   *
   * Accepts a password reset request containing a token and new password, performs the password reset operation,
   * and returns a success response if the reset is successful.
   *
   * @param passwordResetRequestDTO the password reset request containing the token and new password information
   * @return HTTP 200 response with a success response DTO upon successful password reset
   */
  @Override
  @PreAuthorize("permitAll()")
  public ResponseEntity<PasswordResetSuccessResponseDTO> v1PasswordResetPut(
      @Valid PasswordResetRequestDTO passwordResetRequestDTO) {
    log.info("Processing password reset for token: {}", passwordResetRequestDTO.getToken());
    var command = mapper.toCommand(passwordResetRequestDTO);
    resetPasswordUseCase.resetPassword(command);
    var response = mapper.toSuccessResponseForReset(new TokenWrapper(passwordResetRequestDTO.getToken()));
    return ResponseEntity.ok(response);
  }

  /**
   * Validates a password reset token and returns the validation result.
   *
   * @param token the UUID token to be validated
   * @return HTTP 200 response containing the token validation result DTO
   */
  @Override
  @PreAuthorize("permitAll()")
  public ResponseEntity<TokenValidResponseDTO> v1PasswordResetTokenGet(UUID token) {
    log.info("Validating token: {}", token);
    var result = validateTokenUseCase.validatePrimaryToken(token.toString());
    var dto = mapper.toTokenValidResponse(result);
    return ResponseEntity.ok(dto);
  }
}
