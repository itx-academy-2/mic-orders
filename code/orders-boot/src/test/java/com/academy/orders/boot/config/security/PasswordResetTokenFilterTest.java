package com.academy.orders.boot.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenFilterTest {
  private PasswordResetTokenFilter filter;

  private FilterChain filterChain;

  static final String BASE_PATH = "/v1/password-reset/";

  @BeforeEach
  void setUp() {
    filter = new PasswordResetTokenFilter(BASE_PATH);
    filterChain = (request, response) -> {
    };
  }

  @Test
  void shouldReturn401IfTokenMissingForGetRequest() throws ServletException, IOException {
    var request = new MockHttpServletRequest("GET", BASE_PATH);
    request.setServletPath(BASE_PATH.substring(0, BASE_PATH.length() - 1));
    request.setPathInfo(null);
    var response = new MockHttpServletResponse();

    filter.doFilterInternal(request, response, filterChain);

    assertEquals(MockHttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
  }

  @Test
  void shouldReturn400ForInvalidTokenFormat() throws ServletException, IOException {
    String invalidToken = "invalid-token-format";
    var request = new MockHttpServletRequest("GET", BASE_PATH + invalidToken);
    request.setServletPath(BASE_PATH.substring(0, BASE_PATH.length() - 1));
    request.setPathInfo("/" + invalidToken);
    var response = new MockHttpServletResponse();

    filter.doFilterInternal(request, response, filterChain);

    assertEquals(MockHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
  }

  @Test
  void shouldPassFilterIfValidTokenPresentInUrl() throws ServletException, IOException {
    String token = UUID.randomUUID().toString();
    var request = new MockHttpServletRequest("GET", BASE_PATH + token);
    request.setServletPath(BASE_PATH.substring(0, BASE_PATH.length() - 1));
    request.setPathInfo("/" + token);
    var response = new MockHttpServletResponse();

    filter.doFilterInternal(request, response, filterChain);

    assertEquals(MockHttpServletResponse.SC_OK, response.getStatus());
  }

  @Test
  void shouldPassThroughPostRequestWithoutToken() throws ServletException, IOException {
    var request = new MockHttpServletRequest("POST", BASE_PATH);
    request.setServletPath(BASE_PATH.substring(0, BASE_PATH.length() - 1));
    request.setPathInfo(null);
    var response = new MockHttpServletResponse();

    filter.doFilterInternal(request, response, filterChain);

    assertEquals(MockHttpServletResponse.SC_OK, response.getStatus());
  }

  @Test
  void shouldPassThroughPutRequestWithoutToken() throws ServletException, IOException {
    var request = new MockHttpServletRequest("PUT", BASE_PATH);
    request.setServletPath(BASE_PATH.substring(0, BASE_PATH.length() - 1));
    request.setPathInfo(null);
    var response = new MockHttpServletResponse();

    filter.doFilterInternal(request, response, filterChain);

    assertEquals(MockHttpServletResponse.SC_OK, response.getStatus());
  }

  @Test
  void shouldExtractTokenFromQueryParameterAsFallback() throws ServletException, IOException {
    String token = UUID.randomUUID().toString();
    var request = new MockHttpServletRequest("GET", BASE_PATH);
    request.setServletPath(BASE_PATH.substring(0, BASE_PATH.length() - 1));
    request.setPathInfo(null);
    request.setParameter("token", token);
    var response = new MockHttpServletResponse();

    filter.doFilterInternal(request, response, filterChain);

    assertEquals(MockHttpServletResponse.SC_OK, response.getStatus());
  }

  @Test
  void shouldHandleTokenWithTrailingSlash() throws ServletException, IOException {
    String token = UUID.randomUUID().toString();
    var request = new MockHttpServletRequest("GET", BASE_PATH + token + "/");
    request.setServletPath(BASE_PATH.substring(0, BASE_PATH.length() - 1));
    request.setPathInfo("/" + token + "/");
    var response = new MockHttpServletResponse();

    filter.doFilterInternal(request, response, filterChain);

    assertEquals(MockHttpServletResponse.SC_OK, response.getStatus());
  }

  @Test
  void shouldHandleTokenWithQueryParameters() throws ServletException, IOException {
    String token = UUID.randomUUID().toString();
    var request = new MockHttpServletRequest("GET", BASE_PATH + token + "?redirect=true");
    request.setServletPath(BASE_PATH.substring(0, BASE_PATH.length() - 1));
    request.setPathInfo("/" + token);
    var response = new MockHttpServletResponse();

    filter.doFilterInternal(request, response, filterChain);

    assertEquals(MockHttpServletResponse.SC_OK, response.getStatus());
  }
}
