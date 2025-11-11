package com.academy.orders.domain.product.dto;

import java.math.BigDecimal;

public record PriceRangeDto(BigDecimal minPrice, BigDecimal maxPrice) {
}
