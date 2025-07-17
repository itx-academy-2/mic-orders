package com.academy.orders.boot.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {

  /**
   * Provides a system clock set to the UTC time zone as a Spring bean.
   *
   * @return a {@link Clock} instance representing the system UTC clock
   */
  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }
}
