package com.academy.orders.domain.product.usecase;

import com.academy.orders.domain.product.entity.Product;

/**
 * Use case interface for changing product quantity.
 */
public interface ChangeQuantityUseCase {

  /**
   * Adjusts product quantity by a given delta.
   *
   * <ul> <li>Positive delta → decreases quantity (e.g. product reserved or ordered)</li> <li>Negative delta → increases quantity (e.g.
   * reservation canceled)</li> </ul>
   *
   * @param product the {@link Product} whose quantity should be changed
   * @param delta the number of units to subtract (positive) or add (negative)
   */
  void changeQuantityOfProduct(Product product, int delta);
}
