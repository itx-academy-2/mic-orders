package com.academy.orders.domain.product.dto;

import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProductManagementFilterDto(ProductStatus status, String searchByName, BigDecimal priceMore,
    BigDecimal priceLess, Integer quantityMore, Integer quantityLess, LocalDateTime createdAfter,
    LocalDateTime createdBefore, List<String> tags) {
}
