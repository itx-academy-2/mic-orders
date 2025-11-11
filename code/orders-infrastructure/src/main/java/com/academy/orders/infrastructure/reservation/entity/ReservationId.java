package com.academy.orders.infrastructure.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReservationId implements Serializable {

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "product_id", nullable = false)
  private UUID productId;
}
