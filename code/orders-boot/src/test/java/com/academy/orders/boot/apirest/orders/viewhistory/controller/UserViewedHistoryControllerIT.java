package com.academy.orders.boot.apirest.orders.viewhistory.controller;

import com.academy.orders.boot.apirest.orders.common.AbstractControllerIT;
import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import com.academy.orders.infrastructure.account.repository.AccountJpaAdapter;
import com.academy.orders.infrastructure.language.entity.LanguageEntity;
import com.academy.orders.infrastructure.language.repository.LanguageJpaAdapter;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationId;
import com.academy.orders.infrastructure.product.repository.ProductJpaAdapter;
import com.academy.orders.infrastructure.viewhistory.entity.ViewedHistoryEntity;
import com.academy.orders.infrastructure.viewhistory.entity.ViewedHistoryId;
import com.academy.orders.infrastructure.viewhistory.repository.ViewedHistoryJpaAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserViewedHistoryControllerIT extends AbstractControllerIT {

  @Autowired
  private ProductJpaAdapter productJpaAdapter;

  @Autowired
  private LanguageJpaAdapter languageJpaAdapter;

  @Autowired
  private ViewedHistoryJpaAdapter viewedHistoryJpaAdapter;

  @Autowired
  private AccountJpaAdapter accountJpaAdapter;

  @Value("${auth.users[0].username}")
  private String user;

  private UUID testProductId;

  @AfterEach
  void cleanupTestProduct() {
    if (testProductId != null) {
      productJpaAdapter.deleteById(testProductId);
    }
  }

  @Test
  void addProductToViewedHistoryTest() {
    // Given
    UUID productId = createTestProduct();
    HttpHeaders headers = buildAuthHeaders(user);
    String url = baseUrl() + "/v1/my-view-history/" + productId;

    // When
    var result = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(headers), Void.class);

    // Then
    assertEquals(204, result.getStatusCode().value());

    // Check DB
    Long accountId = getUserId();
    Optional<ViewedHistoryEntity> record = viewedHistoryJpaAdapter.findById(
        new ViewedHistoryId(accountId, productId));
    assertTrue(record.isPresent(), "Viewed history should contain the product");

    // Cleanup viewed history
    viewedHistoryJpaAdapter.deleteById(new ViewedHistoryId(accountId, productId));
  }

  @Test
  void deleteViewedProductTest() {
    // Given
    UUID productId = createTestProduct();
    Long accountId = getUserId();
    viewedHistoryJpaAdapter.save(new ViewedHistoryEntity(accountId, productId));
    HttpHeaders headers = buildAuthHeaders(user);
    String url = baseUrl() + "/v1/my-view-history/" + productId;

    // When
    var result = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

    // Then
    assertEquals(204, result.getStatusCode().value());
    assertFalse(viewedHistoryJpaAdapter.findById(new ViewedHistoryId(accountId, productId)).isPresent());
  }

  @Test
  void deleteAllViewedProductsTest() {
    // Given
    UUID productId = createTestProduct();
    Long accountId = getUserId();
    viewedHistoryJpaAdapter.save(new ViewedHistoryEntity(accountId, productId));
    HttpHeaders headers = buildAuthHeaders(user);
    String url = baseUrl() + "/v1/my-view-history";

    // When
    var result = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

    // Then
    assertEquals(204, result.getStatusCode().value());
    assertTrue(viewedHistoryJpaAdapter.findAllByIdAccountId(accountId, org.springframework.data.domain.Pageable.unpaged()).isEmpty());
  }

  @Test
  void getViewedProductsTest() {
    // Given
    UUID productId = createTestProduct();
    Long accountId = getUserId();
    viewedHistoryJpaAdapter.save(new ViewedHistoryEntity(accountId, productId));
    HttpHeaders headers = buildAuthHeaders(user);
    String url = baseUrl() + "/v1/my-view-history?page=0&size=10&lang=en";

    // When
    var result = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Object.class);

    // Then
    assertEquals(200, result.getStatusCode().value());
    assertNotNull(result.getBody());

    // Cleanup
    viewedHistoryJpaAdapter.deleteById(new ViewedHistoryId(accountId, productId));
  }

  private UUID createTestProduct() {
    LanguageEntity language = languageJpaAdapter.findByCode("en")
        .orElseGet(() -> languageJpaAdapter.save(
            LanguageEntity.builder().code("en").build()));

    ProductEntity product = ProductEntity.builder()
        .status(ProductStatus.VISIBLE)
        .image("https://example.com/test.jpg")
        .quantity(10)
        .price(BigDecimal.valueOf(100))
        .build();

    ProductEntity savedProduct = productJpaAdapter.saveAndFlush(product);

    ProductTranslationId translationId = new ProductTranslationId(savedProduct.getId(), language.getId());
    ProductTranslationEntity translation = ProductTranslationEntity.builder()
        .productTranslationId(translationId)
        .product(savedProduct)
        .language(language)
        .name("Test Product")
        .build();

    savedProduct.setProductTranslations(new HashSet<>(List.of(translation)));
    ProductEntity finalSavedProduct = productJpaAdapter.saveAndFlush(savedProduct);
    testProductId = finalSavedProduct.getId();
    return testProductId;
  }

  private Long getUserId() {
    return accountJpaAdapter.findByEmail(user).get().getId();
  }
}
