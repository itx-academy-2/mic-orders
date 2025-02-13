package com.academy.orders.infrastructure.discount.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "discounts")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class DiscountEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private int amount;

  private LocalDateTime startDate;

  private LocalDateTime endDate;
}
