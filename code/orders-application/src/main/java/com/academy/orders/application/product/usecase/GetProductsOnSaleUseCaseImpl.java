package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.GetProductsOnSaleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetProductsOnSaleUseCaseImpl implements GetProductsOnSaleUseCase {
  private final ProductRepository productRepository;

  @Override
  public Page<Product> getProductsOnSale(Pageable pageable, String lang) {
    return productRepository.findProductsWhereDiscountIsNotNull(lang, pageable);
  }
}
