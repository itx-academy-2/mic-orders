package com.academy.orders.apirest.passwordreset.controller;

import com.academy.orders.apirest.auth.util.SecurityUtils;
import com.academy.orders.apirest.common.ErrorHandler;
import com.academy.orders.apirest.common.TestSecurityConfig;
import com.academy.orders.domain.passwordreset.dto.PasswordResetRequestDTO;
import com.academy.orders.domain.passwordreset.dto.TokenValidationResult;
import com.academy.orders.domain.passwordreset.dto.TokenWrapper;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.academy.orders.domain.passwordreset.usecase.ProcessPasswordResetUseCase;
import com.academy.orders.domain.passwordreset.usecase.ResetPasswordUseCase;
import com.academy.orders.domain.passwordreset.usecase.ValidateTokenUseCase;
import com.academy.orders.domain.ratelimit.usecase.ClientIpExtractorUseCase;
import com.academy.orders_api_rest.generated.model.PasswordResetEmailRequestDTO;
import com.academy.orders.apirest.passwordreset.mapper.PasswordResetMapper;
import com.academy.orders_api_rest.generated.model.PasswordResetSuccessResponseDTO;
import com.academy.orders_api_rest.generated.model.TokenValidResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(value = {PasswordResetMapper.class, AopAutoConfiguration.class, TestSecurityConfig.class,
    ErrorHandler.class, SecurityUtils.class})
@ContextConfiguration(classes = {PasswordResetController.class})
@WebMvcTest(PasswordResetController.class)
@AutoConfigureMockMvc
class PasswordResetControllerIT {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ProcessPasswordResetUseCase processPasswordResetUseCase;

  @MockBean
  private ClientIpExtractorUseCase clientIpExtractorUseCase;

  @MockBean
  private ResetPasswordUseCase resetPasswordUseCase;

  @MockBean
  private ValidateTokenUseCase validateTokenUseCase;

  @MockBean
  private PasswordResetMapper passwordResetMapper;

  @MockBean
  private RateLimiterRegistry rateLimiterRegistry;

  static final String BASE_URL = "/v1/password-reset";

  static final String CLIENT_IP = "192.168.1.1";

  static final String EMAIL = "user@example.com";

  static final String TOKEN = "550e8400-e29b-41d4-a716-446655440000";

  static final String NEW_PASSWORD = "StrongPass1!";

  @BeforeEach
  void setUp() {
    reset(processPasswordResetUseCase, clientIpExtractorUseCase, resetPasswordUseCase,
        validateTokenUseCase, passwordResetMapper, rateLimiterRegistry);
    when(clientIpExtractorUseCase.extractClientIp()).thenReturn(CLIENT_IP);

    var rateLimiter = mock(RateLimiter.class);
    when(rateLimiter.acquirePermission()).thenReturn(true);
    when(rateLimiter.getRateLimiterConfig()).thenReturn(RateLimiterConfig.custom()
        .limitForPeriod(5)
        .limitRefreshPeriod(Duration.ofMinutes(1))
        .timeoutDuration(Duration.ofSeconds(1))
        .build());
    when(rateLimiterRegistry.rateLimiter("passwordReset")).thenReturn(rateLimiter);
  }

  @Test
  void v1PasswordResetPost_shouldReturnSuccessResponse_whenEmailIsValid() throws Exception {
    // Given
    var requestDTO = new PasswordResetEmailRequestDTO();
    requestDTO.setEmail(EMAIL);
    var responseDTO = new PasswordResetSuccessResponseDTO();
    responseDTO.setMessage("If the email exists, a password reset link has been sent");
    responseDTO.setToken(null);
    responseDTO.setTimestamp(java.time.OffsetDateTime.now(java.time.ZoneId.of("UTC")));

    when(passwordResetMapper.toSuccessResponseForEmail()).thenReturn(responseDTO);

    // When & Then
    mockMvc.perform(post(BASE_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message")
            .value("If the email exists, a password reset link has been sent"))
        .andExpect(jsonPath("$.token").isEmpty())
        .andExpect(jsonPath("$.timestamp").exists());

    verify(clientIpExtractorUseCase).extractClientIp();
    verify(processPasswordResetUseCase).processPasswordReset(EMAIL, CLIENT_IP);
    verify(passwordResetMapper).toSuccessResponseForEmail();
  }

  @Test
  void v1PasswordResetPost_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
    // Given
    var requestDTO = new PasswordResetEmailRequestDTO();
    requestDTO.setEmail("invalid-email");

    // When & Then
    mockMvc.perform(post(BASE_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.title").value("Bad Request"))
        .andExpect(jsonPath("$.detail").value("must be a well-formed email address"));

    verifyNoInteractions(clientIpExtractorUseCase, processPasswordResetUseCase, passwordResetMapper);
  }

  @Test
  void v1PasswordResetPut_shouldReturnSuccessResponse_whenRequestIsValid() throws Exception {
    // Given
    var requestDTO = new com.academy.orders_api_rest.generated.model.PasswordResetRequestDTO();
    requestDTO.setToken(UUID.fromString(TOKEN));
    requestDTO.setPassword(NEW_PASSWORD);
    var responseDTO = new PasswordResetSuccessResponseDTO();
    responseDTO.setMessage("Password has been successfully reset");
    responseDTO.setToken(TOKEN);
    responseDTO.setTimestamp(java.time.OffsetDateTime.now(java.time.ZoneId.of("UTC")));

    when(passwordResetMapper.toCommand(any(com.academy.orders_api_rest.generated.model.PasswordResetRequestDTO.class)))
        .thenReturn(new PasswordResetRequestDTO(TOKEN, NEW_PASSWORD));
    when(passwordResetMapper.toSuccessResponseForReset(any(TokenWrapper.class)))
        .thenReturn(responseDTO);

    // When & Then
    mockMvc.perform(put(BASE_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Password has been successfully reset"))
        .andExpect(jsonPath("$.token").value(TOKEN))
        .andExpect(jsonPath("$.timestamp").exists());

    verify(passwordResetMapper).toCommand(requestDTO);
    verify(resetPasswordUseCase).resetPassword(any(PasswordResetRequestDTO.class));
    verify(passwordResetMapper).toSuccessResponseForReset(any(TokenWrapper.class));
  }

  @Test
  void v1PasswordResetPut_shouldReturnBadRequest_whenPasswordIsInvalid() throws Exception {
    // Given
    var requestDTO = new com.academy.orders_api_rest.generated.model.PasswordResetRequestDTO();
    requestDTO.setToken(UUID.fromString(TOKEN));
    requestDTO.setPassword("");

    // When & Then
    mockMvc.perform(put(BASE_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(passwordResetMapper, resetPasswordUseCase);
  }

  @Test
  void v1PasswordResetTokenGet_shouldReturnValidResponse_whenTokenIsValid() throws Exception {
    // Given
    var token = UUID.fromString(TOKEN);
    var responseDTO = new TokenValidResponseDTO();
    responseDTO.setValid(true);
    responseDTO.setToken(TOKEN);
    responseDTO.setNewToken(TOKEN);
    responseDTO.setTokenType("PRIMARY");
    responseDTO.setExpiresAt(java.time.OffsetDateTime.now(java.time.ZoneId.of("UTC")).plusHours(1));
    responseDTO.setTimestamp(java.time.OffsetDateTime.now(java.time.ZoneId.of("UTC")));

    var validationResult = new TokenValidationResult(
        true,
        token,
        "PRIMARY",
        Instant.now().plusSeconds(3600),
        token,
        Instant.now());
    when(validateTokenUseCase.validatePrimaryToken(TOKEN)).thenReturn(validationResult);
    when(passwordResetMapper.toTokenValidResponse(any(TokenValidationResult.class))).thenReturn(responseDTO);

    // When & Then
    mockMvc.perform(get(BASE_URL + "/" + TOKEN))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.valid").value(true))
        .andExpect(jsonPath("$.token").value(TOKEN))
        .andExpect(jsonPath("$.newToken").value(TOKEN))
        .andExpect(jsonPath("$.tokenType").value("PRIMARY"))
        .andExpect(jsonPath("$.expiresAt").exists())
        .andExpect(jsonPath("$.timestamp").exists());

    verify(validateTokenUseCase).validatePrimaryToken(TOKEN);
    verify(passwordResetMapper).toTokenValidResponse(validationResult);
  }

  @Test
  void v1PasswordResetTokenGet_shouldReturnInvalidResponse_whenTokenIsInvalid() throws Exception {
    // Given
    var token = UUID.fromString(TOKEN);
    var responseDTO = new TokenValidResponseDTO();
    responseDTO.setValid(false);
    responseDTO.setToken(TOKEN);
    responseDTO.setNewToken(null);
    responseDTO.setTokenType(null);
    responseDTO.setExpiresAt(null);
    responseDTO.setTimestamp(java.time.OffsetDateTime.now(java.time.ZoneId.of("UTC")));

    var validationResult = new TokenValidationResult(
        false, token, null,
        null, null, Instant.now());
    when(validateTokenUseCase.validatePrimaryToken(TOKEN)).thenReturn(validationResult);
    when(passwordResetMapper.toTokenValidResponse(any(TokenValidationResult.class))).thenReturn(responseDTO);

    // When & Then
    mockMvc.perform(get(BASE_URL + "/" + TOKEN))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.valid").value(false))
        .andExpect(jsonPath("$.token").value(TOKEN))
        .andExpect(jsonPath("$.newToken").isEmpty())
        .andExpect(jsonPath("$.tokenType").isEmpty())
        .andExpect(jsonPath("$.expiresAt").isEmpty())
        .andExpect(jsonPath("$.timestamp").exists());

    verify(validateTokenUseCase).validatePrimaryToken(TOKEN);
    verify(passwordResetMapper).toTokenValidResponse(validationResult);
  }
}
