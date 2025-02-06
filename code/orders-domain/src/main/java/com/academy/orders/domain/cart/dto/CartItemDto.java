package com.academy.orders.domain.cart.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CartItemDto(UUID productId, String image, String name, BigDecimal productPrice, Integer quantity,
    BigDecimal calculatedPrice) {
}
