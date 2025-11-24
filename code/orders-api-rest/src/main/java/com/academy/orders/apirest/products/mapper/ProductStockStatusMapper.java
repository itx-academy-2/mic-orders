package com.academy.orders.apirest.products.mapper;

import com.academy.orders.domain.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductStockStatusMapper {

  public String map(Product product) {
    Integer quantity = product.getQuantity();
    return quantity > 0 ? "AVAILABLE" : "ENDED";
  }
}
