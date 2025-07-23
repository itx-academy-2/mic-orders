package com.academy.orders.domain.orderV2.repository;

import com.academy.orders.domain.orderV2.entity.OrderV2;

import java.util.UUID;

/**
 * Repository interface for managing and loading orders, version 2.
 */
public interface OrderV2Repository {
  /**
   * Method saves order V2 to the DB.
   *
   * @param accountId id of logged-in user
   * @param order - {@link OrderV2} to save
   * @return {@link UUID} id of created order
   * @author Oleksandra Bulhakova
   */
  UUID save(OrderV2 order, Long accountId);
}
