package com.academy.orders.domain.wishlist.repository;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import java.util.UUID;

/**
 * Repository interface for accessing and managing user's wishlist.
 */
public interface WishlistRepository {

  /**
   * Adds a product to the user's wishlist. If the product is already present, this operation is a no-op.
   *
   * @param accountId the {@link Long} ID of the user.
   * @param productId the {@link UUID} ID of the product to add.
   */
  void addProductToWishlist(Long accountId, UUID productId);

  /**
   * Removes a specific product from the user's wishlist.
   *
   * @param accountId the {@link Long} ID of the user.
   * @param productId the {@link UUID} ID of the product to remove.
   */
  void removeProductFromWishlist(Long accountId, UUID productId);

  /**
   * Retrieves a paginated list of the user's wishlist products.
   *
   * @param accountId the {@link Long} ID of the user.
   * @param pageable the {@link Pageable} pagination and sorting information.
   * @param language the {@link String} language code for product localization.
   * @return a {@link Page} of {@link Product} in the user's wishlist.
   */
  Page<Product> getWishlistProducts(Long accountId, Pageable pageable, String language);
}
