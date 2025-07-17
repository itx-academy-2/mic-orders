package com.academy.orders.domain.viewhistory.usecase;

import java.util.UUID;

/**
 * Use case interface for deleting a specific product from user's viewed history.
 */
public interface DeleteProductFromViewedHistoryUseCase {

  /**
   * Deletes a specific product from the user's viewed history.
   *
   * @param accountId the {@link Long} ID of the user.
   * @param productId the {@link UUID} ID of the product to delete.
   */
  void deleteProductFromViewedHistory(Long accountId, UUID productId);
}
