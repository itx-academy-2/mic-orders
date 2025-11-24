package com.academy.orders.apirest.products.mapper;

import com.academy.orders.domain.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductStockStatusMapper {

  public String map(Product product) {
    if (product == null) {
      throw new IllegalArgumentException("Product cannot be null");
    }
    Integer quantity = product.getQuantity();
    return quantity != null && quantity > 0 ? "AVAILABLE" : "ENDED";
  }
}
