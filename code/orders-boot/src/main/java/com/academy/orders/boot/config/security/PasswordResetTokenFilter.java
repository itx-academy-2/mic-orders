package com.academy.orders.boot.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter for validating password reset tokens on GET requests to the API. <p> POST and PUT requests bypass token validation. GET requests
 * must include a valid UUID token either in the URL after {@code basePath} or as a query parameter named {@code token}. <p> If the token is
 * missing or invalid, the filter returns the corresponding HTTP status: <ul> <li>401 Unauthorized — token is missing</li> <li>400 Bad
 * Request — token format is invalid</li> </ul>
 */
@Slf4j
@Component
public class PasswordResetTokenFilter extends OncePerRequestFilter {
  private final String basePath;

  private final AntPathRequestMatcher matcher;

  public PasswordResetTokenFilter(@Value("${password-reset.base-path}") String configuredBasePath) {
    this.basePath = normalizePath(configuredBasePath);
    this.matcher = new AntPathRequestMatcher(this.basePath + "**");
  }

  /**
   * Main filter logic. <p> For GET requests, checks for presence and validity of the token. POST and PUT requests are allowed through
   * without validation.
   *
   * @param request the HTTP request
   * @param response the HTTP response
   * @param filterChain the filter chain
   * @throws ServletException if a servlet error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (!matcher.matches(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    log.debug("PasswordResetTokenFilter triggered for: {}", request.getRequestURI());

    String method = request.getMethod();
    String token = extractToken(request);

    if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
      filterChain.doFilter(request, response);
      return;
    }

    if ("GET".equalsIgnoreCase(method)) {
      if (token == null || token.isBlank()) {
        log.warn("Missing password reset token for GET request to: {}", request.getRequestURI());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing password reset token");
        return;
      }

      try {
        UUID.fromString(token);
      } catch (IllegalArgumentException e) {
        log.warn("Invalid token format: {}", token);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid token format");
        return;
      }

      filterChain.doFilter(request, response);
      return;
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extracts the password reset token from the URL or the "token" query parameter.
   *
   * @param request the HTTP request
   * @return the token as a String, or {@code null} if no token is present
   */
  private String extractToken(HttpServletRequest request) {
    String requestURI = request.getRequestURI();
    if (requestURI == null) {
      return null;
    }

    if (requestURI.startsWith(basePath) && requestURI.length() > basePath.length()) {
      String token = requestURI.substring(basePath.length());
      int queryIndex = token.indexOf('?');
      if (queryIndex > 0) {
        token = token.substring(0, queryIndex);
      }
      return token.endsWith("/") ? token.substring(0, token.length() - 1) : token;
    }
    return request.getParameter("token");
  }

  /**
   * Normalizes the base path for the filter.
   *
   * @param path the path from configuration
   * @return a normalized path ending with '/'
   * @throws IllegalArgumentException if the path is null or blank
   */
  private static String normalizePath(String path) {
    if (path == null || path.isBlank()) {
      throw new IllegalArgumentException("password-reset.base-path must be configured");
    }
    path = path.trim();
    return path.endsWith("/") ? path : path + "/";
  }
}
