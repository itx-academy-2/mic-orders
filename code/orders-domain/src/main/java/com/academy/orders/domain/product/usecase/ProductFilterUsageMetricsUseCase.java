package com.academy.orders.domain.product.usecase;

import com.academy.orders.domain.product.dto.ProductsOnSaleFilterDto;

public interface ProductFilterUsageMetricsUseCase {
  void addMetrics(ProductsOnSaleFilterDto filter);
}
