package com.academy.orders.domain.product.dto;

import java.util.UUID;

public record ProductBestsellersDto(UUID productId, Double percentageOfTotalOrders) {

}
