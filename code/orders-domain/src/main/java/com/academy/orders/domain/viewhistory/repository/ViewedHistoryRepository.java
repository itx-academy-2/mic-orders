package com.academy.orders.domain.viewhistory.repository;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;

import java.util.UUID;

/**
 * Repository interface for accessing and managing user's viewed products history.
 */
public interface ViewedHistoryRepository {

  /**
   * Adds a product to the user's viewed history. If the product already exists, updates its viewed timestamp.
   *
   * @param accountId the {@link Long} ID of the user.
   * @param productId the {@link UUID} ID of the product to add.
   */
  void addOrUpdateViewedProduct(Long accountId, UUID productId);

  /**
   * Deletes all products from the user's viewed history.
   *
   * @param accountId the {@link Long} ID of the user.
   */
  void deleteAllViewedProducts(Long accountId);

  /**
   * Deletes a specific product from the user's viewed history.
   *
   * @param accountId the {@link Long} ID of the user.
   * @param productId the {@link UUID} ID of the product to delete.
   */
  void deleteViewedProduct(Long accountId, UUID productId);

  /**
   * Retrieves a paginated list of the user's viewed products, sorted by most recent views first.
   *
   * @param accountId the {@link Long} ID of the user.
   * @param pageable the {@link Pageable} pagination and sorting information.
   * @param language the {@link String} language code for product localization.
   * @return a {@link Page} containing the list of {@link Product}.
   */
  Page<Product> getViewedProducts(Long accountId, Pageable pageable, String language);
}
