package com.academy.orders.domain.reservation.entity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Aggregated metadata for a user's reservations.
 */
public record UserReservationsMetadata(BigDecimal remainingMoney, int remainingItems, List<ReservationMetadata> reservations) {
}
