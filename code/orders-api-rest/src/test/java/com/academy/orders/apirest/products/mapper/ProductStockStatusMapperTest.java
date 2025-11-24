package com.academy.orders.apirest.products.mapper;

import com.academy.orders.domain.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.academy.orders.apirest.ModelUtils.getProductWithQuantity;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductStockStatusMapperTest {

  private ProductStockStatusMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ProductStockStatusMapper();
  }

  @Test
  void shouldReturnAvailableForPositiveQuantityTest() {
    // Given
    Product product = getProductWithQuantity(5);

    // When
    String status = mapper.map(product);

    // Then
    assertEquals("AVAILABLE", status);
  }

  @Test
  void shouldReturnEndedForZeroQuantityTest() {
    // Given
    Product product = getProductWithQuantity(0);

    // When
    String status = mapper.map(product);

    // Then
    assertEquals("ENDED", status);
  }
}
