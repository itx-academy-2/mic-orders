package com.academy.orders.boot.config.rest;

import com.academy.prometheus_api.generated.ApiClient;
import com.academy.prometheus_api.generated.api.FilterStatisticsApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterApiClient {
  @Bean(name = "prometheus")
  public ApiClient prometheusApiClient() {
    return new ApiClient().setBasePath("http://16.170.252.48:9090");
  }

  @Bean
  public FilterStatisticsApi filterAnalyticsApiClient(final @Qualifier("prometheus") ApiClient apiClient) {
    return new FilterStatisticsApi(apiClient);
  }
}
