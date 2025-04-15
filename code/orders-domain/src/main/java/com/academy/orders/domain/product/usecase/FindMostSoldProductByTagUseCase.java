package com.academy.orders.domain.product.usecase;

import com.academy.orders.domain.product.entity.Product;

import java.util.Optional;

public interface FindMostSoldProductByTagUseCase {
  Optional<Product> findMostSoldProductByTag(String language, String tag);
}
