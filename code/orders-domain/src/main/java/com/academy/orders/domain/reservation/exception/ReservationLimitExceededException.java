package com.academy.orders.domain.reservation.exception;

/**
 * Exception thrown when a user tries to reserve more products than allowed.
 */
public class ReservationLimitExceededException extends RuntimeException {
  public ReservationLimitExceededException(int maxAllowed) {
    super("User can reserve maximum " + maxAllowed + " products");
  }
}
