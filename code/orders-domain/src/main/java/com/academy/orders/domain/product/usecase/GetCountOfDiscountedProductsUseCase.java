package com.academy.orders.domain.product.usecase;

/**
 * Use case interface for retrieving the count of discounted products.
 */
public interface GetCountOfDiscountedProductsUseCase {
  /**
   * Retrieves the count of products that have a discount applied. The method returns the total number of products with a discount.
   *
   * @return the count of products that have a discount.
   */
  int getCountOfDiscountedProducts();
}
