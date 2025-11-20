package com.academy.orders.external.pexels.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "pexels")
public class PexelsClientProperties {

  /**
   * API key for authenticating requests to the Pexels API.
   */
  private String apiKey;

  /**
   * Base URL of Pexels API, e.g. "https://api.pexels.com/v1"
   */
  private String baseUrl;

  /**
   * Number of photos per request (default 8).
   */
  private Integer perPage = 8;
}
