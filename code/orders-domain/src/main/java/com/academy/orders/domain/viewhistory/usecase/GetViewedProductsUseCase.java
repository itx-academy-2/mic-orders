package com.academy.orders.domain.viewhistory.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;

/**
 * Use case interface for retrieving viewed products from user's viewed history.
 */
public interface GetViewedProductsUseCase {

  /**
   * Retrieves a paginated list of viewed products for the user, sorted by most recent first.
   *
   * @param accountId the {@link Long} ID of the user.
   * @param pageable the {@link Pageable} object for pagination and sorting information.
   * @param language the {@link String} language code for product localization.
   *
   * @return a {@link Page} containing the list of {@link Product}.
   */
  Page<Product> getViewedProducts(Long accountId, Pageable pageable, String language);
}
