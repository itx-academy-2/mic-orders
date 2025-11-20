package com.academy.orders.infrastructure.pexels.adapter;

import com.academy.orders.domain.pexels.exception.ImageSearchUnavailableException;
import com.academy.orders.domain.pexels.usecase.PexelsImageSearchUseCase;
import com.academy.orders.external.pexels.config.PexelsClientProperties;
import com.academy.orders.external.pexels.dto.PexelsSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PexelsImageSearchClientAdapter implements PexelsImageSearchUseCase {

  private final RestTemplate restTemplate;

  private final PexelsClientProperties properties;

  private final SecureRandom random = new SecureRandom();

  /**
   * Performs image search using Pexels API with a two-step approach: 1) Fetch total number of results (per_page=1 for lightweight call) 2)
   * Pick a random page to introduce natural variation in results 3) Fetch random page images
   */
  @Override
  public List<String> searchImages(String query) {
    try {
      String encodedQuery = UriUtils.encode(query, StandardCharsets.UTF_8);
      HttpEntity<Void> request = createRequestEntity();

      int total = fetchTotalResults(encodedQuery, request);
      if (total == 0) {
        log.info("No results for query '{}'", query);
        return List.of();
      }

      int page = pickRandomPage(total);
      return fetchImages(encodedQuery, page, request);

    } catch (Exception ex) {
      log.error("Pexels API error for query '{}': {}", query, ex.getMessage());
      throw new ImageSearchUnavailableException("Image search unavailable", ex);
    }
  }

  private HttpEntity<Void> createRequestEntity() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", properties.getApiKey());
    return new HttpEntity<>(headers);
  }

  private int fetchTotalResults(String encodedQuery, HttpEntity<Void> request) {
    String url = buildUrl(encodedQuery, 1, 1);

    PexelsSearchResponse body = callPexels(url, request);
    return body != null ? body.getTotalResults() : 0;
  }

  private int pickRandomPage(int totalResults) {
    int perPage = properties.getPerPage();
    int maxPages = (int) Math.ceil((double) totalResults / perPage);
    return (maxPages > 1) ? random.nextInt(maxPages) + 1 : 1;
  }

  private List<String> fetchImages(String encodedQuery, int page, HttpEntity<Void> request) {
    String url = buildUrl(encodedQuery, properties.getPerPage(), page);

    PexelsSearchResponse body = callPexels(url, request);
    if (body == null || body.getPhotos() == null) {
      log.warn("Empty image list for page {}", page);
      return List.of();
    }

    return body.getPhotos().stream()
        .map(p -> p.getSrc().getOriginal())
        .toList();
  }

  private String buildUrl(String encodedQuery, int perPage, int page) {
    return properties.getBaseUrl()
        + "/search?query=" + encodedQuery
        + "&per_page=" + perPage
        + "&page=" + page;
  }

  private PexelsSearchResponse callPexels(String url, HttpEntity<Void> request) {
    ResponseEntity<PexelsSearchResponse> response =
        restTemplate.exchange(url, HttpMethod.GET, request, PexelsSearchResponse.class);
    return response.getBody();
  }
}
