package com.academy.orders.domain.wishlist.usecase;

import java.util.UUID;

/**
 * Use case for adding a product to the user's wishlist.
 */
public interface AddToWishlistUseCase {

  /**
   * Adds the specified product to the wishlist of the given user.
   *
   * @param userId the ID of the user.
   * @param productId the ID of the product to add.
   */
  void addProductToWishlist(Long userId, UUID productId);
}
