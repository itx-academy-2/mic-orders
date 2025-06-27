package com.academy.orders.apirest.auth.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

  private final SecurityUtils securityUtils = new SecurityUtils();

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldReturnUserIdWhenJwtIsValid() {
    // Given
    Jwt jwt = mock(Jwt.class);
    Authentication authentication = mock(Authentication.class);
    when(jwt.getClaim("id")).thenReturn(123L);
    when(authentication.getPrincipal()).thenReturn(jwt);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // When
    Long userId = securityUtils.getAuthenticatedUserId();

    // Then
    assertEquals(123L, userId);
  }

  @Test
  void shouldThrowExceptionWhenAuthenticationIsNull() {
    // Given
    SecurityContextHolder.getContext().setAuthentication(null);

    // When / Then
    ResponseStatusException ex = assertThrows(ResponseStatusException.class, securityUtils::getAuthenticatedUserId);

    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
  }

  @Test
  void shouldThrowExceptionWhenPrincipalIsNotJwt() {
    // Given
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("not-a-jwt");

    SecurityContextHolder.getContext().setAuthentication(authentication);

    // When / Then
    ResponseStatusException ex = assertThrows(ResponseStatusException.class, securityUtils::getAuthenticatedUserId);

    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
  }

  @Test
  void shouldThrowExceptionWhenJwtDoesNotContainIdClaim() {
    // Given
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaim("id")).thenReturn(null);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(jwt);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // When / Then
    ResponseStatusException ex = assertThrows(ResponseStatusException.class, securityUtils::getAuthenticatedUserId);

    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
  }
}
