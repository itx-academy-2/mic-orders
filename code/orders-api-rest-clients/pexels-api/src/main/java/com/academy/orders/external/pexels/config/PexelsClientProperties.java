package com.academy.orders.external.pexels.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "pexels")
@Validated
@Data
public class PexelsClientProperties {

  /**
   * API key for authenticating requests to the Pexels API.
   */
  @NotBlank(message = "Pexels API key must not be blank")
  private String apiKey;

  /**
   * Base URL of Pexels API, e.g. "https://api.pexels.com/v1"
   */
  @NotBlank(message = "Pexels base URL must not be blank")
  private String baseUrl;

  /**
   * Number of photos per request (default 8).
   */
  @NotNull(message = "perPage must not be null")
  @Positive(message = "perPage must be positive")
  private Integer perPage;
}
