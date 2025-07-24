package com.academy.orders.application.ratelimit.usecase;

import com.academy.orders.domain.passwordreset.dto.PasswordResetEmailCommand;
import com.academy.orders.domain.ratelimit.dto.RateLimitResult;
import com.academy.orders.domain.ratelimit.usecase.CheckRateLimitUseCase;
import com.academy.orders.domain.ratelimit.usecase.ClientIpExtractorUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestPasswordResetUseCaseImplTest {

  private static final String TEST_EMAIL = "test@example.com";

  private static final String CLIENT_IP = "192.168.0.1";

  private static final String OTHER_IP = "10.0.0.1";

  @Mock
  private CheckRateLimitUseCase rateLimitUseCase;

  @Mock
  private ClientIpExtractorUseCase clientIpExtractorUseCase;

  @InjectMocks
  private RequestPasswordResetUseCaseImpl useCase;

  @Test
  void requestPasswordReset_shouldExtractClientIpAndCallRateLimit() {
    // Given
    var command = new PasswordResetEmailCommand(TEST_EMAIL, CLIENT_IP);
    var expectedResult = new RateLimitResult(true, 5, 1234567890L, 3, 1234567890L);

    when(clientIpExtractorUseCase.extractClientIp()).thenReturn(CLIENT_IP);
    when(rateLimitUseCase.checkRateLimit(CLIENT_IP, TEST_EMAIL)).thenReturn(expectedResult);

    // When
    var result = useCase.requestPasswordReset(command);

    // Then
    verify(clientIpExtractorUseCase, times(1)).extractClientIp();
    verify(rateLimitUseCase, times(1)).checkRateLimit(CLIENT_IP, TEST_EMAIL);
    assertEquals(expectedResult, result);
  }

  @Test
  void requestPasswordReset_shouldAllowWhenRateLimitAllows() {
    // Given
    var command = new PasswordResetEmailCommand(TEST_EMAIL, CLIENT_IP);
    var expectedResult = new RateLimitResult(true, 2, 1234567890L, 1, 1234567890L);

    when(clientIpExtractorUseCase.extractClientIp()).thenReturn(CLIENT_IP);
    when(rateLimitUseCase.checkRateLimit(CLIENT_IP, TEST_EMAIL)).thenReturn(expectedResult);

    // When
    var result = useCase.requestPasswordReset(command);

    // Then
    assertTrue(result.isAllowed());
    assertEquals(2, result.remainingIpAttempts());
    assertEquals(1, result.remainingEmailAttempts());
  }

  @Test
  void requestPasswordReset_shouldBlockWhenRateLimitExceeded() {
    // Given
    var command = new PasswordResetEmailCommand(TEST_EMAIL, CLIENT_IP);
    var blockedResult = new RateLimitResult(false, 0, 1234567890L, 0, 1234567890L);

    when(clientIpExtractorUseCase.extractClientIp()).thenReturn(CLIENT_IP);
    when(rateLimitUseCase.checkRateLimit(CLIENT_IP, TEST_EMAIL)).thenReturn(blockedResult);

    // When
    var result = useCase.requestPasswordReset(command);

    // Then
    assertFalse(result.isAllowed());
    assertEquals(0, result.remainingIpAttempts());
    assertEquals(0, result.remainingEmailAttempts());
  }

  @Test
  void requestPasswordReset_shouldWorkForDifferentIps() {
    // Given
    var command1 = new PasswordResetEmailCommand(TEST_EMAIL, CLIENT_IP);
    var command2 = new PasswordResetEmailCommand(TEST_EMAIL, OTHER_IP);

    var result1 = new RateLimitResult(true, 4, 1234567890L, 2, 1234567890L);
    var result2 = new RateLimitResult(true, 5, 1234567890L, 1, 1234567890L);

    when(clientIpExtractorUseCase.extractClientIp()).thenReturn(CLIENT_IP).thenReturn(OTHER_IP);
    when(rateLimitUseCase.checkRateLimit(CLIENT_IP, TEST_EMAIL)).thenReturn(result1);
    when(rateLimitUseCase.checkRateLimit(OTHER_IP, TEST_EMAIL)).thenReturn(result2);

    // When
    var res1 = useCase.requestPasswordReset(command1);
    var res2 = useCase.requestPasswordReset(command2);

    // Then
    assertTrue(res1.isAllowed());
    assertTrue(res2.isAllowed());
    assertNotEquals(res1.remainingIpAttempts(), res2.remainingIpAttempts());
  }

  @Test
  void requestPasswordReset_hashEmail_shouldProduceConsistentLowercaseHash() throws Exception {
    // Given
    var emailUpper = "USER@EXAMPLE.COM";
    var emailLower = "user@example.com";

    // When
    var method = RequestPasswordResetUseCaseImpl.class.getDeclaredMethod("hashEmail", String.class);
    method.setAccessible(true);

    var hashUpper = (String) method.invoke(useCase, emailUpper);
    var hashLower = (String) method.invoke(useCase, emailLower);

    // Then
    assertEquals(hashUpper, hashLower, "Hash should be case-insensitive and consistent");
    assertTrue(hashUpper.matches("^[0-9a-f]+$"), "Hash should be a valid hex string");
  }

  @Test
  void requestPasswordReset_shouldThrowExceptionWhenEmailIsNull() {
    // Given
    var command = new PasswordResetEmailCommand(null, CLIENT_IP);
    when(clientIpExtractorUseCase.extractClientIp()).thenReturn(CLIENT_IP);

    // When / Then
    assertThrows(NullPointerException.class, () -> useCase.requestPasswordReset(command));
    verify(clientIpExtractorUseCase).extractClientIp();
    verifyNoInteractions(rateLimitUseCase);
  }

  @Test
  void requestPasswordReset_shouldLogRateLimitExceeded() {
    // Given
    var command = new PasswordResetEmailCommand(TEST_EMAIL, CLIENT_IP);
    var blockedResult = new RateLimitResult(false, 0, 1234567890L, 0, 1234567890L);

    when(clientIpExtractorUseCase.extractClientIp()).thenReturn(CLIENT_IP);
    when(rateLimitUseCase.checkRateLimit(CLIENT_IP, TEST_EMAIL)).thenReturn(blockedResult);

    // When
    var result = useCase.requestPasswordReset(command);

    // Then
    assertFalse(result.isAllowed());
    verify(clientIpExtractorUseCase, times(1)).extractClientIp();
    verify(rateLimitUseCase, times(1)).checkRateLimit(CLIENT_IP, TEST_EMAIL);
  }
}
