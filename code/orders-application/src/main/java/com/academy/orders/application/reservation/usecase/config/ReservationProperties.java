package com.academy.orders.application.reservation.usecase.config;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import java.math.BigDecimal;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "reservations")
public class ReservationProperties {

  @NotNull(message = "reservations.max-items must be configured")
  @Min(value = 1, message = "reservations.max-items must be at least 1")
  private Integer maxReservedProducts;

  @NotNull(message = "reservations.max-money must be configured")
  @DecimalMin(value = "0.0", inclusive = false,
      message = "reservations.max-money must be greater than 0")
  private BigDecimal maxTotalCost;
}
