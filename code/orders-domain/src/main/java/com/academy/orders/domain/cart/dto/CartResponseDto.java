package com.academy.orders.domain.cart.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CartResponseDto(List<CartItemDto> items, BigDecimal totalPrice, BigDecimal totalPriceWithDiscount) {
}
