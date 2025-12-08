package com.academy.orders.infrastructure.reservation.entity.projection;

import java.time.Instant;

/**
 * Projection representing reservation data joined with user data.
 */
public interface ReservationWithUserProjection {
  String getUsername();

  String getEmail();

  Instant getAddedAt();

  /**
   * Currently, multiple-item reservations are not implemented. So each reservation always represents exactly 1 item.
   */
  default int getQuantity() {
    return 1;
  }
}
