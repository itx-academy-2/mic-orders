package com.academy.orders.apirest.products.mapper;

import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.entity.Tag;
import com.academy.orders_api_rest.generated.model.ProductAvailabilityDTO;
import com.academy.orders_api_rest.generated.model.ProductDetailsResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.Collections;

import static com.academy.orders.apirest.ModelUtils.getProduct;
import static com.academy.orders.apirest.ModelUtils.getProductWithDiscount;
import static com.academy.orders.apirest.ModelUtils.getProductWithEmptyTags;
import static com.academy.orders.apirest.ModelUtils.getProductWithEmptyTranslations;
import static com.academy.orders.apirest.TestConstants.PRODUCT_DESCRIPTION;
import static com.academy.orders.apirest.TestConstants.PRODUCT_NAME;
import static com.academy.orders.apirest.TestConstants.TAG_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductDetailsResponseDTOMapperTest {
  private ProductDetailsResponseDTOMapper productDetailsResponseDTOMapper;

  @BeforeEach
  void setUp() {
    productDetailsResponseDTOMapper = Mappers.getMapper(ProductDetailsResponseDTOMapper.class);
  }

  @Test
  void toDtoWithValidProductTest() {
    // Given
    var product = getProduct();

    // When
    var dto = productDetailsResponseDTOMapper.toDTO(product);

    // Then
    assertEquals(PRODUCT_NAME, dto.getName());
    assertEquals(PRODUCT_DESCRIPTION, dto.getDescription());
    assertEquals(product.getImage(), dto.getImage());
    assertIterableEquals(product.getTags().stream().map(Tag::name).toList(), dto.getTags());
    assertEquals(product.getQuantity(), dto.getQuantity());
    assertEquals(product.getPrice(), dto.getPrice());
    assertNull(dto.getDiscount());
    assertNull(dto.getPriceWithDiscount());
    assertStatus(product, dto);
  }

  @Test
  void toDtoWithDiscountTest() {
    // Given
    var product = getProductWithDiscount();

    // When
    var dto = productDetailsResponseDTOMapper.toDTO(product);

    // Then
    assertEquals(PRODUCT_NAME, dto.getName());
    assertEquals(PRODUCT_DESCRIPTION, dto.getDescription());
    assertEquals(product.getImage(), dto.getImage());
    assertIterableEquals(product.getTags().stream().map(Tag::name).toList(), dto.getTags());
    assertEquals(product.getQuantity(), dto.getQuantity());
    assertEquals(product.getPrice(), dto.getPrice());
    assertEquals(product.getDiscount().getAmount(), dto.getDiscount());
    assertEquals(product.getPriceWithDiscount(), dto.getPriceWithDiscount());
    assertStatus(product, dto);
  }

  @Test
  void toDtoWithEmptyTranslationsTest() {
    // Given
    var product = getProductWithEmptyTranslations();

    // When
    var dto = productDetailsResponseDTOMapper.toDTO(product);

    // Then
    assertNull(dto.getName());
    assertNull(dto.getDescription());
    assertEquals(product.getImage(), dto.getImage());
    assertEquals(1, dto.getTags().size());
    assertEquals(TAG_NAME, dto.getTags().get(0));
    assertEquals(product.getQuantity(), dto.getQuantity());
    assertEquals(product.getPrice(), dto.getPrice());
    assertNull(dto.getDiscount());
    assertNull(dto.getPriceWithDiscount());
    assertStatus(product, dto);
  }

  @Test
  void toDtoWithEmptyTagsTest() {
    // Given
    var product = getProductWithEmptyTags();

    // When
    var dto = productDetailsResponseDTOMapper.toDTO(product);

    // Then
    assertEquals(PRODUCT_NAME, dto.getName());
    assertEquals(PRODUCT_DESCRIPTION, dto.getDescription());
    assertEquals(product.getImage(), dto.getImage());
    assertEquals(Collections.EMPTY_LIST, dto.getTags());
    assertEquals(product.getQuantity(), dto.getQuantity());
    assertEquals(product.getPrice(), dto.getPrice());
    assertNull(dto.getDiscount());
    assertEquals(product.getPriceWithDiscount(), dto.getPriceWithDiscount());
    assertStatus(product, dto);
  }

  private void assertStatus(Product product, ProductDetailsResponseDTO dto) {
    ProductAvailabilityDTO expected =
        (product.getQuantity() != null && product.getQuantity() > 0)
            ? ProductAvailabilityDTO.AVAILABLE
            : ProductAvailabilityDTO.ENDED;

    Assertions.assertEquals(expected, dto.getAvailability());
  }
}
