package com.academy.orders.infrastructure.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Table
@Data
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ProductTranslationId implements Serializable {
  @Column(name = "product_id")
  private UUID productId;

  @Column(name = "language_id")
  private Long languageId;
}
