package com.academy.orders.domain.product.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DiscountAndPriceWithDiscountRangeDto(
    BigDecimal minimumPriceWithDiscount,
    BigDecimal maximumPriceWithDiscount,
    Integer minimumDiscount,
    Integer maximumDiscount) {

}
