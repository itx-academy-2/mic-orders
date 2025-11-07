package com.academy.orders.domain.product.dto;

import java.math.BigDecimal;
import java.util.List;

public record PageProductsDto<T> (
    Long totalElements,
    Integer totalPages,
    Boolean first,
    Boolean last,
    Integer number,
    Integer numberOfElements,
    Integer size,
    Boolean empty,
    BigDecimal minProductPrice,
    BigDecimal maxProductPrice,
    List<T> content) {
}
