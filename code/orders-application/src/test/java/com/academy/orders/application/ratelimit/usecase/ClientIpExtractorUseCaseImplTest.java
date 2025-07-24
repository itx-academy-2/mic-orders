package com.academy.orders.application.ratelimit.usecase;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class ClientIpExtractorUseCaseImplTest {
  private ClientIpExtractorUseCaseImpl extractor;

  private AutoCloseable mocks;

  @Mock
  private ServletRequestAttributes servletRequestAttributes;

  @Mock
  private HttpServletRequest request;

  @Mock
  private RequestAttributes nonServletRequestAttributes;

  @BeforeEach
  void setUp() {
    mocks = MockitoAnnotations.openMocks(this);
    extractor = new ClientIpExtractorUseCaseImpl();
  }

  @AfterEach
  void tearDown() throws Exception {
    if (mocks != null) {
      mocks.close();
    }
  }

  @Test
  void extractClientIp_returnsUnknown_whenNoRequestAttributes() {
    // Given
    RequestContextHolder.resetRequestAttributes();

    // When
    String result = extractor.extractClientIp();

    // Then
    assertEquals("UNKNOWN", result);
  }

  @Test
  void extractClientIp_returnsUnknown_whenRequestAttributesAreNotServletRequestAttributes() {
    // Given
    RequestContextHolder.setRequestAttributes(nonServletRequestAttributes);

    // When
    String result = extractor.extractClientIp();

    // Then
    assertEquals("UNKNOWN", result);
  }

  @ParameterizedTest
  @CsvSource({
      ", 127.0.0.1, 127.0.0.1",
      "'   ', 127.0.0.1, 127.0.0.1",
      ", '', ''"
  })
  void extractClientIp_returnsRemoteAddr_forVariousHeaderAndRemoteAddr(String xForwardedFor, String remoteAddr,
      String expected) {
    when(servletRequestAttributes.getRequest()).thenReturn(request);
    RequestContextHolder.setRequestAttributes(servletRequestAttributes);

    String header = (xForwardedFor == null || xForwardedFor.isBlank()) ? null : xForwardedFor;

    when(request.getHeader("X-Forwarded-For")).thenReturn(header);
    when(request.getRemoteAddr()).thenReturn(remoteAddr);

    String result = extractor.extractClientIp();

    assertEquals(expected, result);
  }

  @Test
  void extractClientIp_returnsSingleIp_whenXForwardedForSingleIp() {
    // Given
    when(servletRequestAttributes.getRequest()).thenReturn(request);
    RequestContextHolder.setRequestAttributes(servletRequestAttributes);

    String headerValue = "203.0.113.195";
    when(request.getHeader("X-Forwarded-For")).thenReturn(headerValue);

    // When
    String result = extractor.extractClientIp();

    // Then
    assertEquals("203.0.113.195", result);
  }

  @Test
  void extractClientIp_handlesMultipleSpacesAndEmptySegments() {
    // Given
    when(servletRequestAttributes.getRequest()).thenReturn(request);
    RequestContextHolder.setRequestAttributes(servletRequestAttributes);

    String headerValue = "  ,  , 198.51.100.17 , 10.0.0.2";
    when(request.getHeader("X-Forwarded-For")).thenReturn(headerValue);

    // When
    String result = extractor.extractClientIp();

    // Then
    assertEquals("", result);
  }

  @Test
  void extractClientIp_returnsRemoteAddr_whenRemoteAddrIsEmpty() {
    // Given
    when(servletRequestAttributes.getRequest()).thenReturn(request);
    RequestContextHolder.setRequestAttributes(servletRequestAttributes);

    when(request.getHeader("X-Forwarded-For")).thenReturn(null);
    when(request.getRemoteAddr()).thenReturn("");

    // When
    String result = extractor.extractClientIp();

    // Then
    assertEquals("", result);
  }

  @Test
  void extractClientIp_returnsRemoteAddr_whenRemoteAddrIsNull() {
    // Given
    when(servletRequestAttributes.getRequest()).thenReturn(request);
    RequestContextHolder.setRequestAttributes(servletRequestAttributes);

    when(request.getHeader("X-Forwarded-For")).thenReturn(null);
    when(request.getRemoteAddr()).thenReturn(null);

    // When
    String result = extractor.extractClientIp();

    // Then
    assertNull(result);
  }
}
