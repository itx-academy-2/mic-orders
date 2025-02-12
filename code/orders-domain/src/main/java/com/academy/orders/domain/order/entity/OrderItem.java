package com.academy.orders.domain.order.entity;

import com.academy.orders.domain.product.entity.Product;
import lombok.Builder;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Builder
public record OrderItem(Product product, BigDecimal price, Integer discount, Integer quantity) {
  public BigDecimal getPriceWithDiscount() {
    if (discount == null) {
      return null;
    }
    final BigDecimal percentage = BigDecimal.valueOf(100 - discount).divide(BigDecimal.valueOf(100));
    return price.multiply(percentage).setScale(2, RoundingMode.HALF_DOWN);
  }
}
