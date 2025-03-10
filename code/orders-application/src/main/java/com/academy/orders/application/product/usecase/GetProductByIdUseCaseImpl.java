package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.exception.ProductNotFoundException;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.GetProductByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetProductByIdUseCaseImpl implements GetProductByIdUseCase {
  private final ProductRepository productRepository;

  @Override
  public Product getProductById(UUID productId) {
    return productRepository.getById(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));
  }
}
