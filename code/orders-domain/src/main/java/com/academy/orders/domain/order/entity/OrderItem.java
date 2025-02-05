package com.academy.orders.domain.order.entity;

import com.academy.orders.domain.product.entity.Product;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItem(Product product, BigDecimal price, Integer quantity) {
}
