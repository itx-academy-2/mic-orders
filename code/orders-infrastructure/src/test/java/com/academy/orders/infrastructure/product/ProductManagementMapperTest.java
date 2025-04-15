package com.academy.orders.infrastructure.product;

import com.academy.orders.domain.product.entity.Language;
import com.academy.orders.domain.product.entity.ProductTranslationManagement;
import com.academy.orders.infrastructure.language.entity.LanguageEntity;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.UUID;

import static com.academy.orders.infrastructure.ModelUtils.getProductManagement;
import static com.academy.orders.infrastructure.ModelUtils.getProductEntity;
import static com.academy.orders.infrastructure.ModelUtils.getProductTranslationManagement;

class ProductManagementMapperTest {
  private ProductManagementMapper productManagementMapper;

  @BeforeEach
  void setUp() {
    productManagementMapper = Mappers.getMapper(ProductManagementMapper.class);
  }

  @Test
  void fromEntityTest() {
    var productEntity = new ProductEntity();
    productEntity.setId(UUID.randomUUID());

    var translationEntity = new ProductTranslationEntity();
    translationEntity.setProductTranslationId(new ProductTranslationId(productEntity.getId(), 1L));
    translationEntity.setName("Test Translation");
    translationEntity.setDescription("Test Description");

    productEntity.setProductTranslations(Set.of(translationEntity));

    var productManagement = productManagementMapper.fromEntity(productEntity);
    Assertions.assertNotNull(productManagement);
    Assertions.assertEquals(productEntity.getId(), productManagement.id());
  }

  @Test
  void toEntityTest() {
    var productTranslationManagement = getProductTranslationManagement();
    var productManagement = getProductManagement();
    var productEntity = productManagementMapper.toEntity(productManagement);
    var translationEntity = productEntity.getProductTranslations().iterator().next();

    Assertions.assertNotNull(productEntity);
    Assertions.assertEquals(productManagement.id(), productEntity.getId());
    Assertions.assertNotNull(productEntity.getProductTranslations());
    Assertions.assertEquals(1, productEntity.getProductTranslations().size());
    Assertions.assertEquals(productTranslationManagement.productId(),
        translationEntity.getProductTranslationId().getProductId());
    Assertions.assertEquals(productTranslationManagement.languageId(),
        translationEntity.getProductTranslationId().getLanguageId());
    Assertions.assertEquals(productTranslationManagement.name(), translationEntity.getName());
    Assertions.assertEquals(productTranslationManagement.description(), translationEntity.getDescription());
  }

  @Test
  void mapProductTranslationManagementWithNullsTest() {
    final Set<ProductTranslationEntity> result = productManagementMapper.mapProductTranslationManagement(null, null);
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void productTranslationManagementWithNullsTest() {
    ProductTranslationEntity result = productManagementMapper.productTranslationManagement(null, null);
    Assertions.assertNull(result);
  }

  @Test
  void mapProductTranslationManagementTest() {
    final ProductTranslationManagement productTranslationManagement = getProductTranslationManagement();
    final ProductEntity productEntity = getProductEntity();
    final Set<ProductTranslationEntity> result = productManagementMapper
        .mapProductTranslationManagement(Set.of(productTranslationManagement), productEntity);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.size());

    var translationEntity = result.iterator().next();
    Assertions.assertEquals(productTranslationManagement.productId(),
        translationEntity.getProductTranslationId().getProductId());
    Assertions.assertEquals(productTranslationManagement.languageId(),
        translationEntity.getProductTranslationId().getLanguageId());
    Assertions.assertEquals(productTranslationManagement.name(), translationEntity.getName());
    Assertions.assertEquals(productTranslationManagement.description(), translationEntity.getDescription());
    Assertions.assertSame(productEntity, translationEntity.getProduct());
  }

  @Test
  void productTranslationManagementTest() {
    final ProductTranslationManagement productTranslationManagement = getProductTranslationManagement();
    final ProductEntity productEntity = getProductEntity();
    final ProductTranslationEntity result = productManagementMapper
        .productTranslationManagement(productTranslationManagement, productEntity);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(productTranslationManagement.productId(),
        result.getProductTranslationId().getProductId());
    Assertions.assertEquals(productTranslationManagement.languageId(),
        result.getProductTranslationId().getLanguageId());
    Assertions.assertEquals(productTranslationManagement.name(), result.getName());
    Assertions.assertSame(productEntity, result.getProduct());
    Assertions.assertEquals(productTranslationManagement.description(), result.getDescription());
  }

  @Test
  void mapLanguageTest() {
    var language = new Language(1L, "en");
    LanguageEntity result = productManagementMapper.mapLanguage(language);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(language.id(), result.getId());
    Assertions.assertEquals(language.code(), result.getCode());
  }

  @Test
  void mapLanguageWithNullTest() {
    LanguageEntity result = productManagementMapper.mapLanguage(null);
    Assertions.assertNull(result);
  }

  @Test
  void mapProductTest() {
    UUID productId = UUID.randomUUID();
    ProductEntity result = productManagementMapper.mapProduct(productId);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(productId, result.getId());
  }

  @Test
  void mapProductWithNullTest() {
    ProductEntity result = productManagementMapper.mapProduct(null);
    Assertions.assertNull(result);
  }
}
