package com.academy.orders.domain.product.usecase;

import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.PageProductsWithPriceRangeDto;
import com.academy.orders.domain.product.dto.ProductFilterDto;
import com.academy.orders.domain.product.entity.Product;

/**
 * Use case interface for getting all prodcuts.
 */
public interface GetAllProductsUseCase {
  /**
   * Retrieves a paginated list of products based on the provided language, pagination details, and tags.
   *
   * @param language the language code for localizing product details.
   * @param pageable the {@link Pageable} object for pagination and sorting information.
   * @param filter the {@link ProductFilterDto} containing filtering criteria (for example tags). Can be empty to retrieve products without
   *        additional filtering.
   * @return a {@link PageProductsWithPriceRangeDto} of {@link Product} objects that match the specified criteria.
   * @author Anton Bodnar, Yurii Osovskyi, Denys Ryhal
   */

  PageProductsWithPriceRangeDto<Product> getAllProducts(String language, Pageable pageable, ProductFilterDto filter);
}
