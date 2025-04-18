package com.academy.orders.domain.product.entity;

import com.academy.orders.domain.discount.entity.Discount;
import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
@ToString
public class Product {
  private UUID id;

  private ProductStatus status;

  @With
  private String image;

  private LocalDateTime createdAt;

  private Integer quantity;

  private BigDecimal price;

  @Setter
  private Discount discount;

  private Set<Tag> tags;

  private Set<ProductTranslation> productTranslations;

  private Integer version;

  @Setter
  private Double percentageOfTotalOrders;

  public BigDecimal getPriceWithDiscount() {
    if (discount == null) {
      return null;
    }
    final BigDecimal percentage = BigDecimal.valueOf(100 - discount.getAmount()).divide(BigDecimal.valueOf(100));
    return price.multiply(percentage).setScale(2, RoundingMode.HALF_DOWN);
  }

  public Integer getDiscountAmount() {
    if (discount == null) {
      return null;
    }
    return discount.getAmount();
  }
}
