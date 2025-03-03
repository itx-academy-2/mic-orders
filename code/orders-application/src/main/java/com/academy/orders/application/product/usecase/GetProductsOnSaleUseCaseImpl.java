package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.ProductsOnSaleFilterDto;
import com.academy.orders.domain.product.dto.ProductsOnSaleResponseDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.GetProductsOnSaleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class GetProductsOnSaleUseCaseImpl implements GetProductsOnSaleUseCase {
  private final ProductRepository productRepository;

  @Override
  public ProductsOnSaleResponseDto getProductsOnSale(ProductsOnSaleFilterDto filter, Pageable pageable, String lang) {
    Page<Product> page = productRepository.findProductsWhereDiscountIsNotNull(filter, lang, pageable);
    return ProductsOnSaleResponseDto.builder()
        .minimumPriceWithDiscount(BigDecimal.ONE)
        .maximumPriceWithDiscount(BigDecimal.TEN)
        .pageProducts(page)
        .build();
  }
}
