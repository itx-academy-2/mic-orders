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

@Entity
@Table(name = "user_reservations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationEntity {

  @EmbeddedId
  private ReservationId id;

  @Column(name = "added_at", nullable = false)
  private Instant addedAt;
}
