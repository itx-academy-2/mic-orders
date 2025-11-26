package com.academy.orders.apirest.products.mapper;

import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.entity.Tag;
import com.academy.orders_api_rest.generated.model.ProductPreviewDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static com.academy.orders.apirest.ModelUtils.getProduct;
import static com.academy.orders.apirest.ModelUtils.getProductTranslation;
import static com.academy.orders.apirest.ModelUtils.getProductWithDiscount;
import static com.academy.orders.apirest.ModelUtils.getProductWithEmptyTags;
import static com.academy.orders.apirest.ModelUtils.getProductWithEmptyTranslations;
import static com.academy.orders.apirest.ModelUtils.getProductWithNullTranslations;
import static com.academy.orders.apirest.TestConstants.PRODUCT_DESCRIPTION;
import static com.academy.orders.apirest.TestConstants.PRODUCT_NAME;
import static com.academy.orders.apirest.TestConstants.TAG_NAME;

class ProductPreviewDTOMapperTest {
  private ProductPreviewDTOMapper productPreviewDTOMapper;

  @BeforeEach
  void setUp() {
    productPreviewDTOMapper = Mappers.getMapper(ProductPreviewDTOMapper.class);
  }

  @Test
  void toDtoWithValidProductTest() {
    // Given
    var product = getProduct();

    // When
    var dto = productPreviewDTOMapper.toDto(product);

    // Then
    Assertions.assertEquals(PRODUCT_NAME, dto.getName());
    Assertions.assertEquals(PRODUCT_DESCRIPTION, dto.getDescription());
    Assertions.assertEquals(1, dto.getTags().size());
    Assertions.assertEquals(TAG_NAME, dto.getTags().get(0));
    Assertions.assertNull(dto.getDiscount());
    Assertions.assertNull(dto.getPriceWithDiscount());
    assertStatus(product, dto);
  }

  @Test
  void toDtoWithDiscount() {
    // Given
    var product = getProductWithDiscount();

    // When
    var dto = productPreviewDTOMapper.toDto(product);

    // Then
    Assertions.assertEquals(PRODUCT_NAME, dto.getName());
    Assertions.assertEquals(PRODUCT_DESCRIPTION, dto.getDescription());
    Assertions.assertEquals(1, dto.getTags().size());
    Assertions.assertEquals(TAG_NAME, dto.getTags().get(0));
    Assertions.assertEquals(product.getDiscount().getAmount(), dto.getDiscount());
    Assertions.assertEquals(product.getPriceWithDiscount(), dto.getPriceWithDiscount());
    assertStatus(product, dto);
  }

  @Test
  void toDtoWithEmptyTranslationsTest() {
    // Given
    var product = getProductWithEmptyTranslations();

    // When
    var dto = productPreviewDTOMapper.toDto(product);

    // Then
    Assertions.assertNull(dto.getName());
    Assertions.assertNull(dto.getDescription());
    Assertions.assertEquals(1, dto.getTags().size());
    Assertions.assertEquals(TAG_NAME, dto.getTags().get(0));
    Assertions.assertNull(dto.getDiscount());
    Assertions.assertNull(dto.getPriceWithDiscount());
    assertStatus(product, dto);
  }

  @Test
  void toDtoWithEmptyTagsTest() {
    // Given
    var product = getProductWithEmptyTags();

    // When
    var dto = productPreviewDTOMapper.toDto(product);

    // Then
    Assertions.assertEquals(PRODUCT_NAME, dto.getName());
    Assertions.assertEquals(PRODUCT_DESCRIPTION, dto.getDescription());
    Assertions.assertEquals(0, dto.getTags().size());
    Assertions.assertNull(dto.getDiscount());
    Assertions.assertNull(dto.getPriceWithDiscount());
    assertStatus(product, dto);
  }

  @Test
  void toDtoWithNullTranslationsTest() {
    // Given
    var product = getProductWithNullTranslations();

    // When
    var dto = productPreviewDTOMapper.toDto(product);

    // Then
    Assertions.assertNull(dto.getName());
    Assertions.assertNull(dto.getDescription());
    Assertions.assertEquals(1, dto.getTags().size());
    Assertions.assertEquals(TAG_NAME, dto.getTags().get(0));
    Assertions.assertNull(dto.getDiscount());
    Assertions.assertNull(dto.getPriceWithDiscount());
    assertStatus(product, dto);
  }

  private void assertStatus(Product product, ProductPreviewDTO dto) {
    String expected = (product.getQuantity() != null && product.getQuantity() > 0)
        ? "AVAILABLE"
        : "ENDED";

    Assertions.assertEquals(expected, dto.getAvailability().getValue().toUpperCase());
  }
}
