package com.academy.orders.domain.reservation.usecase;

import com.academy.orders.domain.reservation.entity.UserReservationsMetadata;

/**
 * Use case for retrieving metadata about user's reserved products.
 */
public interface GetUserReservationsMetadataUseCase {

  /**
   * Retrieves metadata about the given user's reserved products, including remaining limits and reservation timestamps.
   *
   * @param userId the ID of the user
   * @return {@link UserReservationsMetadata} containing remaining money, remaining items, and list of reservations
   */
  UserReservationsMetadata getUserReservationsMetadata(Long userId);
}
