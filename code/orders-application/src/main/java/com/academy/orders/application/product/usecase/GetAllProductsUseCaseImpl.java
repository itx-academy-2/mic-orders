package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.GetAllProductsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllProductsUseCaseImpl implements GetAllProductsUseCase {
  private final ProductRepository productRepository;

  @Override
  public Page<Product> getAllProducts(String language, Pageable pageable, List<String> tags) {
    Page<Product> products;
    if (pageable.sort().isEmpty()) {
      products = productRepository.findAllProductsWithDefaultSorting(language, pageable, tags);
    } else {
      products = productRepository.findAllProducts(language, pageable, tags);
    }
    return products;
  }
}
