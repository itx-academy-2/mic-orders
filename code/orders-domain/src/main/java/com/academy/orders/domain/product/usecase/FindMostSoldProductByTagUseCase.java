package com.academy.orders.domain.product.usecase;

import com.academy.orders.domain.product.entity.Product;

public interface FindMostSoldProductByTagUseCase {
  Product findMostSoldProductByTag(String language, String tag);
}
