package com.academy.orders.domain.viewhistory.usecase;

/**
 * Use case interface for deleting all viewed products from user's viewed history.
 */
public interface DeleteAllViewedProductsUseCase {

  /**
   * Deletes all products from the user's viewed history.
   *
   * @param accountId the {@link Long} ID of the user.
   */
  void deleteAllViewedProducts(Long accountId);
}
