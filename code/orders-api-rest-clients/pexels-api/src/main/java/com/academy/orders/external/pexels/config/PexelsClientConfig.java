package com.academy.orders.external.pexels.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(PexelsClientProperties.class)
public class PexelsClientConfig {

  @Bean
  public RestTemplate pexelsRestTemplate() {
    return new RestTemplate();
  }
}
