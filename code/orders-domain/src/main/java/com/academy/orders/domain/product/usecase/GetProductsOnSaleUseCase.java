package com.academy.orders.domain.product.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.ProductsOnSaleFilterDto;
import com.academy.orders.domain.product.dto.ProductsOnSaleResponseDto;

/**
 * Use case interface for getting products on sale.
 */
public interface GetProductsOnSaleUseCase {
  /**
   * Retrieves a paginated list of products that are currently on sale.
   *
   * @param filter containing filter criteria
   * @param pageable the pagination information (e.g., page number, size)
   * @param lang the language code to localize product details (e.g., "en" for English, "uk" for Ukrainian)
   * @return a {@link Page} containing the products on sale, paginated according to the provided {@link Pageable}
   */
  ProductsOnSaleResponseDto getProductsOnSale(ProductsOnSaleFilterDto filter, Pageable pageable, String lang);
}
