package com.academy.orders.domain.orderV2.usecase;

import com.academy.orders.domain.orderV2.dto.CreateOrderV2Dto;

import java.util.UUID;

/**
 * Use case interface for creating new order, V2.
 */
public interface CreateOrderV2UseCase {
    /**
     * Method creates new order.
     *
     * @param order {@link CreateOrderV2Dto}
     * @param accountId id of the user.
     * @author Oleksandra Bulhakova
     */
    UUID createOrderV2(CreateOrderV2Dto order, Long accountId);
}
