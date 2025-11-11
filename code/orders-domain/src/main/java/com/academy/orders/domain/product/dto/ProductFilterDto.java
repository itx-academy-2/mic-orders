package com.academy.orders.domain.product.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProductFilterDto(
    List<String> tags,
    Boolean discount,
    Boolean nonDiscount,
    BigDecimal priceMin,
    BigDecimal priceMax,
    Boolean availability,
    Boolean nonAvailability,
    Boolean deliveryNovaPost,
    Boolean deliveryUkrPost) {
}
