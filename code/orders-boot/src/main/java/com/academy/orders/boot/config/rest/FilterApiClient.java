package com.academy.orders.boot.config.rest;

import com.academy.prometheus_api.generated.ApiClient;
import com.academy.prometheus_api.generated.api.FilterStatisticsApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterApiClient {
  private final String prometheusUrl;

  public FilterApiClient(@Value("${PROMETHEUS_URL}") String prometheusUrl) {
    this.prometheusUrl = prometheusUrl;
  }

  @Bean(name = "prometheus")
  public ApiClient prometheusApiClient() {
    return new ApiClient().setBasePath(prometheusUrl);
  }

  @Bean
  public FilterStatisticsApi filterAnalyticsApiClient(final @Qualifier("prometheus") ApiClient apiClient) {
    return new FilterStatisticsApi(apiClient);
  }
}
