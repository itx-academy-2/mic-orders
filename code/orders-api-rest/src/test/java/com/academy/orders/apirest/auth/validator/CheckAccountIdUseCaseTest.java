package com.academy.orders.apirest.auth.validator;

import com.academy.orders.apirest.auth.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckAccountIdUseCaseTest {
  @InjectMocks
  private CheckAccountIdUseCaseImpl checkAccountIdUseCase;

  @Mock
  private SecurityUtils securityUtils;

  @Test
  void isValidReturnsTrueWhenAccountIdAreSameTest() {
    // Given
    Long userId = 1L;
    when(securityUtils.getAuthenticatedUserId()).thenReturn(userId);

    // When
    boolean valid = checkAccountIdUseCase.hasSameId(userId);

    // Then
    assertTrue(valid);
  }

  @Test
  void isValidReturnsFalseTest() {
    // Given
    Long userId = 1L;
    Long enteredId = 2L;
    when(securityUtils.getAuthenticatedUserId()).thenReturn(userId);

    // When
    boolean valid = checkAccountIdUseCase.hasSameId(enteredId);

    // Then
    assertFalse(valid);
  }
}
