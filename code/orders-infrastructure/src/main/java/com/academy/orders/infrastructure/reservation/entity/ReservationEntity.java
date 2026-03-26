package com.academy.orders.infrastructure.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_reservations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationEntity {

  @EmbeddedId
  private ReservationId id;

  @Column(name = "added_at", nullable = false, updatable = false)
  private Instant addedAt;

  @Column(name = "quantity", nullable = false)
  private int quantity;

  public static ReservationEntity create(Long userId, UUID productId) {
    return ReservationEntity.builder()
        .id(new ReservationId(userId, productId))
        .addedAt(Instant.now())
        .quantity(1)
        .build();
  }

  public void increaseQuantity(int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Delta must be positive");
    }
    this.quantity += amount;
  }

  public void decreaseQuantity(int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    if (this.quantity < amount) {
      throw new IllegalStateException("Cannot decrease quantity below zero");
    }
    this.quantity -= amount;
  }
}
