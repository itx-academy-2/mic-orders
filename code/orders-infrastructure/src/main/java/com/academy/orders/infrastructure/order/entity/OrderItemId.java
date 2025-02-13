package com.academy.orders.infrastructure.order.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
@NoArgsConstructor
@ToString
public class OrderItemId implements Serializable {
  private UUID orderId;

  private UUID productId;
}
