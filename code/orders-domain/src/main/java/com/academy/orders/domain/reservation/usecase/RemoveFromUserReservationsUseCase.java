package com.academy.orders.domain.reservation.usecase;

import java.util.UUID;

/**
 * Use case for removing a product from the user's reservations.
 */
public interface RemoveFromUserReservationsUseCase {

  /**
   * Removes the specified product from the reservations of the given user.
   *
   * @param userId the ID of the user.
   * @param productId the ID of the product to remove.
   */
  void removeProductFromReservations(Long userId, UUID productId);
}
