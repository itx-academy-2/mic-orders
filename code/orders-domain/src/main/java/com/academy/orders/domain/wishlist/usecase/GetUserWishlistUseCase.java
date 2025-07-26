package com.academy.orders.domain.wishlist.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;

/**
 * Use case for retrieving the products in the user's wishlist.
 */
public interface GetUserWishlistUseCase {

  /**
   * Retrieves a paginated list of products in the user's wishlist.
   *
   * @param userId the ID of the user.
   * @param language the language code for product localization.
   * @param pageable pagination and sorting information.
   * @return a page of wishlist products
   */
  Page<Product> getProductsInUserWishlist(Long userId, String language, Pageable pageable);
}
