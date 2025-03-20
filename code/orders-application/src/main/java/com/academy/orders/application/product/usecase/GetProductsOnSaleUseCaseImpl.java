package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.ProductsOnSaleFilterDto;
import com.academy.orders.domain.product.dto.ProductsOnSaleResponseDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.GetProductsOnSaleUseCase;
import com.academy.orders.domain.product.usecase.ProductFilterUsageMetricsUseCase;
import com.academy.orders.domain.product.usecase.SetPercentageOfTotalOrdersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetProductsOnSaleUseCaseImpl implements GetProductsOnSaleUseCase {
  private final ProductRepository productRepository;

  private final ProductFilterUsageMetricsUseCase productFilterUsageMetricsUseCase;

  private final SetPercentageOfTotalOrdersUseCase setPercentageOfTotalOrdersUseCase;

  @Override
  public ProductsOnSaleResponseDto getProductsOnSale(ProductsOnSaleFilterDto filter, Pageable pageable, String lang) {
    final Page<Product> page = productRepository.findProductsWhereDiscountIsNotNull(filter, lang, pageable);

    setPercentageOfTotalOrdersUseCase.setPercentOfTotalOrders(page.content());

    var range = productRepository.findDiscountAndPriceWithDiscountRange();
    productFilterUsageMetricsUseCase.addMetrics(filter);
    return ProductsOnSaleResponseDto.builder()
        .minimumPriceWithDiscount(range.minimumPriceWithDiscount())
        .maximumPriceWithDiscount(range.maximumPriceWithDiscount())
        .minimumDiscount(range.minimumDiscount())
        .maximumDiscount(range.maximumDiscount())
        .pageProducts(page)
        .build();
  }
}
