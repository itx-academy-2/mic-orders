package com.academy.orders.domain.discount.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@Builder
public class Discount {
  private UUID id;

  private int amount;

  private LocalDateTime startDate;

  private LocalDateTime endDate;

  public static boolean isCorrectAmount(final int amount) {
    return amount >= 0 && amount <= 100;
  }
}
