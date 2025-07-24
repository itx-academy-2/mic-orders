package com.academy.orders.boot.apirest.orders.wishlist.controller;

import com.academy.orders.boot.apirest.orders.common.AbstractControllerIT;
import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import com.academy.orders.infrastructure.account.repository.AccountJpaAdapter;
import com.academy.orders.infrastructure.language.entity.LanguageEntity;
import com.academy.orders.infrastructure.language.repository.LanguageJpaAdapter;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationId;
import com.academy.orders.infrastructure.product.repository.ProductJpaAdapter;
import com.academy.orders.infrastructure.wishlist.entity.WishlistEntity;
import com.academy.orders.infrastructure.wishlist.entity.WishlistId;
import com.academy.orders.infrastructure.wishlist.repository.WishlistJpaAdapter;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserWishlistControllerIT extends AbstractControllerIT {

  private static final String BASE_WISHLIST_URL = "/v1/my-wishlist";

  @Autowired
  private ProductJpaAdapter productJpaAdapter;

  @Autowired
  private WishlistJpaAdapter wishlistJpaAdapter;

  @Autowired
  private LanguageJpaAdapter languageJpaAdapter;

  @Autowired
  private AccountJpaAdapter accountJpaAdapter;

  @Value("${auth.users[0].username}")
  private String user;

  private UUID testProductId;

  @AfterEach
  void cleanupTestProduct() {
    if (testProductId != null) {
      wishlistJpaAdapter.deleteById(new WishlistId(getUserId(), testProductId));
      productJpaAdapter.deleteById(testProductId);
    }
  }

  @Test
  void addProductToWishlistTest() {
    // Given
    UUID productId = createTestProduct();
    HttpHeaders headers = buildAuthHeaders(user);
    String url = baseUrl() + BASE_WISHLIST_URL + "/" + productId;

    // When
    var result = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(headers), Void.class);

    // Then
    assertEquals(204, result.getStatusCode().value());

    // Check DB
    Long accountId = getUserId();
    assertTrue(wishlistJpaAdapter.existsById(new WishlistId(accountId, productId)));
  }

  @Test
  void removeProductFromWishlistTest() {
    // Given
    UUID productId = createTestProduct();
    Long accountId = getUserId();
    wishlistJpaAdapter.save(new WishlistEntity(accountId, productId));
    HttpHeaders headers = buildAuthHeaders(user);
    String url = baseUrl() + BASE_WISHLIST_URL + "/" + productId;

    // When
    var result = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

    // Then
    assertEquals(204, result.getStatusCode().value());
    assertFalse(wishlistJpaAdapter.existsById(new WishlistId(accountId, productId)));
  }

  @Test
  void getUserWishlistTest() {
    // Given
    UUID productId = createTestProduct();
    Long accountId = getUserId();
    wishlistJpaAdapter.save(new WishlistEntity(accountId, productId));
    HttpHeaders headers = buildAuthHeaders(user);
    String url = baseUrl() + BASE_WISHLIST_URL + "?page=0&size=10&sort=product.price,DESC&lang=en";

    // When
    var result = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Object.class);

    // Then
    assertEquals(200, result.getStatusCode().value());
    assertNotNull(result.getBody());

    // Cleanup
    wishlistJpaAdapter.delete(new WishlistEntity(accountId, productId));
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
    return accountJpaAdapter.findByEmail(user).orElseThrow().getId();
  }
}
