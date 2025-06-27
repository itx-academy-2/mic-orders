package com.academy.orders.apirest.auth.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
public class SecurityUtils {
  static final String USER_ID_CLAIM = "id";

  static final String ERROR_NOT_AUTHENTICATED = "User not authenticated";

  static final String ERROR_INVALID_ID_FORMAT = "Invalid user ID format in token";

  public Long getAuthenticatedUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
      log.warn("Authentication is missing or principal is not a Jwt instance: {}", authentication);
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ERROR_NOT_AUTHENTICATED);
    }

    Object idClaim = jwt.getClaim(USER_ID_CLAIM);
    if (!(idClaim instanceof Number idNumber)) {
      log.warn("Invalid format for '{}' claim in JWT: {}", USER_ID_CLAIM, idClaim);
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ERROR_INVALID_ID_FORMAT);
    }

    log.debug("Authenticated user ID: {}", idNumber.longValue());
    return idNumber.longValue();
  }
}
