package com.academy.orders.domain.product.usecase;

import com.academy.orders.domain.product.entity.Product;

import java.util.List;

public interface SetPercentageOfTotalOrdersUseCase {
  void setPercentOfTotalOrders(List<Product> page);

  void setPercentOfTotalOrders(Product product);
}
