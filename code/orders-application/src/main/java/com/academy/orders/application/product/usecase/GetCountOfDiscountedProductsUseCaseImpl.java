package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.GetCountOfDiscountedProductsUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GetCountOfDiscountedProductsUseCaseImpl implements GetCountOfDiscountedProductsUseCase {
  private ProductRepository productRepository;

  @Override
  public int getCountOfDiscountedProducts() {
    return productRepository.countByDiscountIsNotNull();
  }
}
