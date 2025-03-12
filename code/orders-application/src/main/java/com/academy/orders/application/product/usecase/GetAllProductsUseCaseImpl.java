package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.ProductBestsellersDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.GetAllProductsUseCase;
import com.academy.orders.domain.product.usecase.GetProductBestsellersUseCase;
import com.academy.orders.domain.product.usecase.SetPercentageOfTotalOrdersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllProductsUseCaseImpl implements GetAllProductsUseCase {
  private final ProductRepository productRepository;

  private final GetProductBestsellersUseCase getProductBestsellersUseCase;

  private final SetPercentageOfTotalOrdersUseCase setPercentageOfTotalOrdersUseCase;

  @Override
  public Page<Product> getAllProducts(String language, Pageable pageable, List<String> tags) {
    Page<Product> products;
    if (pageable.sort().isEmpty()) {
      products = productRepository.findAllProductsWithDefaultSorting(language, pageable, tags);
    } else {
      products = productRepository.findAllProducts(language, pageable, tags);
    }
    setPercentageOfTotalOrdersUseCase.setPercentOfTotalOrders(products.content());
    return products;
  }

}
