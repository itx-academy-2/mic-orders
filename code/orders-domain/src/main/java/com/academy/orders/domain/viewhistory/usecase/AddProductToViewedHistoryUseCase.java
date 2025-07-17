package com.academy.orders.domain.viewhistory.usecase;

import java.util.UUID;

/**
 * Use case interface for adding a product to user's viewed history.
 */
public interface AddProductToViewedHistoryUseCase {

  /**
   * Adds a product to the user's viewed history if already added then update adding date.
   *
   * @param accountId the {@link Long} ID of the user.
   * @param productId the {@link UUID} ID of the product to add.
   */
  void addProductToViewedHistory(Long accountId, UUID productId);
}
