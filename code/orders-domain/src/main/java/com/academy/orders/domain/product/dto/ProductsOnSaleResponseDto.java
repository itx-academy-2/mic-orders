package com.academy.orders.domain.product.dto;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.product.entity.Product;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductsOnSaleResponseDto(
    BigDecimal minimumPriceWithDiscount,
    BigDecimal maximumPriceWithDiscount,
    Page<Product> pageProducts) {
}
