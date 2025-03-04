package com.academy.orders.domain.product.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProductsOnSaleFilterDto(
    List<String> tags,
    Integer minimumDiscount,
    Integer maximumDiscount,
    BigDecimal minimumPriceWithDiscount,
    BigDecimal maximumPriceWithDiscount) {
}
