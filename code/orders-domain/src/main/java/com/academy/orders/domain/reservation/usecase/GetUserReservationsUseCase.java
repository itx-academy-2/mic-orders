package com.academy.orders.domain.reservation.usecase;

import com.academy.orders.domain.product.entity.Product;

import java.util.List;

/**
 * Use case for retrieving the list of products reserved by the user.
 */
public interface GetUserReservationsUseCase {

  /**
   * Retrieves the list of products reserved by the given user.
   *
   * @param userId the ID of the user.
   * @param language the language code for product localization.
   * @return list of products reserved by the user.
   */
  List<Product> getUserReservations(Long userId, String language);
}
