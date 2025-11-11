package com.academy.orders.domain.reservation.usecase;

import java.util.UUID;

/**
 * Use case for adding a product to the user's reservations.
 */
public interface AddToUserReservationsUseCase {

  /**
   * Adds the specified product to the reservations of the given user.
   *
   * @param userId the ID of the user.
   * @param productId the ID of the product to add.
   */
  void addProductToReservations(Long userId, UUID productId);
}
