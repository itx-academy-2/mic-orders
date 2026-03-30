package com.academy.orders.domain.reservation.usecase;

import java.util.UUID;

/**
 * Use case for decreasing quantity of a reserved product for the user.
 */
public interface DecreaseProductReservationQuantityUseCase {

  /**
   * Decreases reserved quantity of the specified product for the given user by 1. If quantity becomes zero, the reservation is removed.
   *
   * @param userId the ID of the user
   * @param productId the ID of the product
   */
  void decreaseReservationQuantity(Long userId, UUID productId);
}
