package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.ProductManagementFilterDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.GetManagerProductsUseCase;
import com.academy.orders.domain.product.usecase.SetPercentageOfTotalOrdersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetManagerProductsUseCaseImpl implements GetManagerProductsUseCase {
  private final ProductRepository productRepository;

  private final SetPercentageOfTotalOrdersUseCase setPercentageOfTotalOrdersUseCase;

  @Override
  public Page<Product> getManagerProducts(Pageable pageable, ProductManagementFilterDto filter, String lang) {
    final Page<Product> page = productRepository.findAllByLanguageWithFilter(lang, filter, pageable);
    setPercentageOfTotalOrdersUseCase.setPercentOfTotalOrders(page.content());
    return page;
  }
}
