package com.academy.orders.infrastructure.pexels.adapter;

import com.academy.orders.domain.pexels.exception.ImageSearchUnavailableException;
import com.academy.orders.external.pexels.config.PexelsClientProperties;
import com.academy.orders.external.pexels.dto.PexelsPhotoItem;
import com.academy.orders.external.pexels.dto.PexelsSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PexelsImageSearchClientAdapterTest {

  private RestTemplate restTemplate;

  private PexelsClientProperties properties;

  private PexelsImageSearchClientAdapter adapter;

  @BeforeEach
  void setup() {
    restTemplate = mock(RestTemplate.class);
    properties = mock(PexelsClientProperties.class);

    when(properties.getApiKey()).thenReturn("API-KEY");
    when(properties.getBaseUrl()).thenReturn("https://api.pexels.com/v1");
    when(properties.getPerPage()).thenReturn(8);

    adapter = new PexelsImageSearchClientAdapter(restTemplate, properties);
  }

  @Test
  void shouldReturnImageUrlsSuccessfullyTest() {
    // Given
    String query = "iphone";

    PexelsSearchResponse firstResponse = new PexelsSearchResponse();
    firstResponse.setTotalResults(16);

    when(restTemplate.exchange(contains("per_page=1"), eq(HttpMethod.GET), any(HttpEntity.class), eq(PexelsSearchResponse.class)))
        .thenReturn(ResponseEntity.ok(firstResponse));

    PexelsSearchResponse secondResponse = new PexelsSearchResponse();
    PexelsPhotoItem.Src src = new PexelsPhotoItem.Src();
    src.setOriginal("https://img.com/1.jpg");

    PexelsPhotoItem photo = new PexelsPhotoItem();
    photo.setSrc(src);
    secondResponse.setPhotos(List.of(photo));

    when(restTemplate.exchange(contains("per_page=8"), eq(HttpMethod.GET), any(HttpEntity.class), eq(PexelsSearchResponse.class)))
        .thenReturn(ResponseEntity.ok(secondResponse));

    // When
    List<String> result = adapter.searchImages(query);

    // Then
    assertEquals(1, result.size());
    assertEquals("https://img.com/1.jpg", result.get(0));
  }

  @Test
  void shouldReturnEmptyListWhenNoResultsTest() {
    // Given
    PexelsSearchResponse firstResponse = new PexelsSearchResponse();
    firstResponse.setTotalResults(0);

    when(restTemplate.exchange(contains("per_page=1"), eq(HttpMethod.GET), any(HttpEntity.class), eq(PexelsSearchResponse.class)))
        .thenReturn(ResponseEntity.ok(firstResponse));

    // When
    List<String> result = adapter.searchImages("iphone");

    // Then
    assertTrue(result.isEmpty());
    verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(), eq(PexelsSearchResponse.class));
  }

  @Test
  void shouldReturnEmptyListWhenBodyIsNullInFirstCallTest() {
    // Given
    when(restTemplate.exchange(contains("per_page=1"), eq(HttpMethod.GET), any(HttpEntity.class), eq(PexelsSearchResponse.class)))
        .thenReturn(ResponseEntity.ok(null));

    // When
    List<String> result = adapter.searchImages("iphone");

    // Then
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyListWhenSecondCallPhotoListIsNullTest() {
    // Given
    PexelsSearchResponse firstResponse = new PexelsSearchResponse();
    firstResponse.setTotalResults(10);

    when(restTemplate.exchange(contains("per_page=1"), eq(HttpMethod.GET), any(HttpEntity.class), eq(PexelsSearchResponse.class)))
        .thenReturn(ResponseEntity.ok(firstResponse));

    PexelsSearchResponse secondResponse = new PexelsSearchResponse();
    secondResponse.setPhotos(null);

    when(restTemplate.exchange(contains("per_page=8"), eq(HttpMethod.GET), any(HttpEntity.class), eq(PexelsSearchResponse.class)))
        .thenReturn(ResponseEntity.ok(secondResponse));

    // When
    List<String> result = adapter.searchImages("iphone");

    // Then
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyListWhenSecondCallBodyIsNullTest() {
    // Given
    PexelsSearchResponse firstResponse = new PexelsSearchResponse();
    firstResponse.setTotalResults(8);

    when(restTemplate.exchange(contains("per_page=1"), eq(HttpMethod.GET), any(HttpEntity.class), eq(PexelsSearchResponse.class)))
        .thenReturn(ResponseEntity.ok(firstResponse));
    when(restTemplate.exchange(contains("per_page=8"), eq(HttpMethod.GET), any(HttpEntity.class), eq(PexelsSearchResponse.class)))
        .thenReturn(ResponseEntity.ok(null));

    // When
    List<String> result = adapter.searchImages("iphone");

    // Then
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldThrowImageSearchUnavailableExceptionOnRestErrorTest() {
    // Given
    when(restTemplate.exchange(anyString(), any(), any(), eq(PexelsSearchResponse.class))).thenThrow(new RuntimeException("API error"));

    // When / Then
    assertThrows(ImageSearchUnavailableException.class,
        () -> adapter.searchImages("iphone"));
  }

  @Test
  void shouldSendAuthorizationHeaderTest() {
    // Given
    PexelsSearchResponse firstResponse = new PexelsSearchResponse();
    firstResponse.setTotalResults(5);

    when(restTemplate.exchange(contains("per_page=1"), eq(HttpMethod.GET), any(HttpEntity.class), eq(PexelsSearchResponse.class)))
        .thenReturn(ResponseEntity.ok(firstResponse));

    PexelsSearchResponse secondResponse = new PexelsSearchResponse();
    secondResponse.setPhotos(List.of());

    when(restTemplate.exchange(contains("per_page=8"), eq(HttpMethod.GET), any(HttpEntity.class), eq(PexelsSearchResponse.class)))
        .thenReturn(ResponseEntity.ok(secondResponse));

    ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);

    // When
    adapter.searchImages("iphone");

    // Then
    verify(restTemplate, atLeastOnce()).exchange(anyString(), eq(HttpMethod.GET), captor.capture(), eq(PexelsSearchResponse.class));
    HttpHeaders headers = captor.getValue().getHeaders();
    assertEquals("API-KEY", headers.getFirst("Authorization"));
  }
}
