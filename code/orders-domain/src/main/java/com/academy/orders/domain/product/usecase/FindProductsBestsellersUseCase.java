package com.academy.orders.domain.product.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;

public interface FindProductsBestsellersUseCase {
  Page<Product> findProductsBestsellers(Pageable pageable, final String language);
}
