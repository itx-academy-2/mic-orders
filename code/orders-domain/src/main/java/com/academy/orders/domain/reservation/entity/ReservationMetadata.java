package com.academy.orders.domain.reservation.entity;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a single reserved product's metadata.
 */
public record ReservationMetadata(UUID productId, Instant reservedAt) {
}
