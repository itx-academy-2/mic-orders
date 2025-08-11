package com.academy.orders.boot.infrastructure.common.repository;

import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Validator;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("it")
public abstract class AbstractRepositoryIT {
  @MockBean
  private SecurityFilterChain securityFilterChain;

  @MockBean
  private MeterRegistry meterRegistry;

  @MockBean
  private RateLimiterRegistry rateLimiterRegistry;

  @MockBean
  private Validator validator;
}
