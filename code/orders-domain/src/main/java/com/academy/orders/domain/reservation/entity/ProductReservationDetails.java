package com.academy.orders.domain.reservation.entity;

import lombok.Builder;
import java.time.Instant;

@Builder
public record ProductReservationDetails(String username, String email, int quantity, Instant addedAt) {
}
