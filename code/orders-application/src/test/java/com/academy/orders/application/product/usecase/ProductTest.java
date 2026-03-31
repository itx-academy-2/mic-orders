package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.entity.enumerated.ProductAvailability;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductTest {

  @Test
  void shouldReturnEndedWhenQuantityIsNull() {
    // Given
    Product product = Product.builder()
        .quantity(null)
        .build();

    // When
    ProductAvailability availability = product.getAvailability();

    // Then
    assertEquals(ProductAvailability.ENDED, availability);
  }

  @Test
  void shouldReturnEndedWhenQuantityIsZero() {
    // Given
    Product product = Product.builder()
        .quantity(0)
        .build();

    // When
    ProductAvailability availability = product.getAvailability();

    // Then
    assertEquals(ProductAvailability.ENDED, availability);
  }

  @Test
  void shouldReturnEndedWhenQuantityIsNegative() {
    // Given
    Product product = Product.builder()
        .quantity(-5)
        .build();

    // When
    ProductAvailability availability = product.getAvailability();

    // Then
    assertEquals(ProductAvailability.ENDED, availability);
  }

  @Test
  void shouldReturnEndSoonWhenQuantityIsOne() {
    // Given
    Product product = Product.builder()
        .quantity(1)
        .build();

    // When
    ProductAvailability availability = product.getAvailability();

    // Then
    assertEquals(ProductAvailability.END_SOON, availability);
  }

  @Test
  void shouldReturnAvailableWhenQuantityGreaterThanOne() {
    // Given
    Product product = Product.builder()
        .quantity(5)
        .build();

    // When
    ProductAvailability availability = product.getAvailability();

    // Then
    assertEquals(ProductAvailability.AVAILABLE, availability);
  }
}
