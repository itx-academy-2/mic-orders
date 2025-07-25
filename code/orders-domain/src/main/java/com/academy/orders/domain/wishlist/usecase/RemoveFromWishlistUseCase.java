package com.academy.orders.domain.wishlist.usecase;

import java.util.UUID;

/**
 * Use case for removing a product from the user's wishlist.
 */
public interface RemoveFromWishlistUseCase {

  /**
   * Removes the specified product from the wishlist of the given user.
   *
   * @param userId the ID of the user.
   * @param productId the ID of the product to remove.
   */
  void removeProductFromWishlist(Long userId, UUID productId);
}
