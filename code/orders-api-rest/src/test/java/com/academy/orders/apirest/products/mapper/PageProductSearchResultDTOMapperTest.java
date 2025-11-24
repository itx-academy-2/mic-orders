package com.academy.orders.apirest.products.mapper;

import com.academy.orders.domain.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import static com.academy.orders.apirest.ModelUtils.getProductWithQuantity;
import static com.academy.orders.apirest.ModelUtils.getProductsPageWithQuantities;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PageProductSearchResultDTOMapperTest {

  private PageProductSearchResultDTOMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = Mappers.getMapper(PageProductSearchResultDTOMapper.class);

    ReflectionTestUtils.setField(mapper, "managementProductMapper", Mappers.getMapper(ManagementProductMapper.class));
    ReflectionTestUtils.setField(mapper, "productStockStatusMapper", new ProductStockStatusMapper());
  }

  @Test
  void shouldMapProductPageWithCorrectStatus() {
    // Given
    Product p1 = getProductWithQuantity(10);
    Product p2 = getProductWithQuantity(0);
    var page = getProductsPageWithQuantities(p1, p2);

    // When
    var dtoPage = mapper.toDto(page);

    // Then
    assertEquals("AVAILABLE", dtoPage.getContent().get(0).getStatus().getValue().toUpperCase());
    assertEquals("ENDED", dtoPage.getContent().get(1).getStatus().getValue().toUpperCase());
  }
}
