package com.academy.orders.infrastructure.product.dto;

import java.math.BigDecimal;

/**
 * Projection returned from the persistence adapter containing minimal and maximal product price values calculated directly by the database.
 */
public record PriceRangeProjection(BigDecimal minPrice, BigDecimal maxPrice) {
}
