package com.academy.orders.apirest.products.mapper;

import com.academy.orders.domain.product.entity.Product;
import com.academy.orders_api_rest.generated.model.ProductAvailabilityStatusDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductAvailabilityMapperTest {

  private ProductAvailabilityMapper productAvailabilityMapper;

  @BeforeEach
  void setUp() {
    productAvailabilityMapper = Mappers.getMapper(ProductAvailabilityMapper.class);
  }

  @Test
  void mapWithAvailableProductTest() {
    // Given
    Product product = Product.builder()
        .quantity(10)
        .build();

    // When
    ProductAvailabilityStatusDTO status = productAvailabilityMapper.map(product);

    // Then
    assertEquals(ProductAvailabilityStatusDTO.AVAILABLE, status);
  }

  @Test
  void mapWithEndedProductTest() {
    // Given
    Product product = Product.builder()
        .quantity(0)
        .build();

    // When
    ProductAvailabilityStatusDTO status = productAvailabilityMapper.map(product);

    // Then
    assertEquals(ProductAvailabilityStatusDTO.ENDED, status);
  }
}
