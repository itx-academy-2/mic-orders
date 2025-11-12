package com.academy.orders.domain.reservation.exception;

import java.math.BigDecimal;

/**
 * Exception thrown when total cost of reserved products exceeds the allowed limit.
 */
public class ReservationTotalCostExceededException extends RuntimeException {
  public ReservationTotalCostExceededException(BigDecimal maxAllowed) {
    super("Reservation total cost limit exceeded: max allowed " + maxAllowed);
  }
}
