package com.academy.orders.apirest.products.mapper;

import com.academy.orders.domain.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

  @Test
  void shouldReturnEndedForNegativeQuantityTest() {
    // Given
    Product product = getProductWithQuantity(-10);

    // When
    String status = mapper.map(product);

    // Then
    assertEquals("ENDED", status);
  }

  @Test
  void shouldReturnEndedWhenQuantityIsNullTest() {
    // Given
    Product product = getProductWithQuantity(null);

    // When
    String status = mapper.map(product);

    // Then
    assertEquals("ENDED", status);
  }

  @Test
  void shouldThrowExceptionWhenProductIsNullTest() {
    // When / Then
    assertThrows(IllegalArgumentException.class, () -> mapper.map(null));
  }

  private Product getProductWithQuantity(Integer quantity) {
    Product product = Product.builder().quantity(quantity).build();
    return product;
  }
}
