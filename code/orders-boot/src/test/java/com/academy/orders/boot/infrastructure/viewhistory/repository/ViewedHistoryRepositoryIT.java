package com.academy.orders.boot.infrastructure.viewhistory.repository;

import com.academy.orders.boot.infrastructure.common.repository.AbstractRepositoryIT;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import com.academy.orders.domain.viewhistory.repository.ViewedHistoryRepository;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ViewedHistoryRepositoryIT extends AbstractRepositoryIT {

  @Value("${auth.users[0].username}")
  private String user;

  private static final String TEST_LANGUAGE = "en";

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private AccountJpaAdapter accountJpaAdapter;

  @Autowired
  private ViewedHistoryRepository viewedHistoryRepository;

  @Autowired
  private ViewedHistoryJpaAdapter viewedHistoryJpaAdapter;

  @Autowired
  private ProductJpaAdapter productJpaAdapter;

  @Autowired
  private LanguageJpaAdapter languageJpaAdapter;

  @Test
  void addOrUpdateViewedProductTest() {
    // Given
    UUID productId = createTestProduct();
    Long testAccountId = getUserId();

    // When: add to history
    viewedHistoryRepository.addOrUpdateViewedProduct(testAccountId, productId);

    // Then: verify it's saved
    Optional<ViewedHistoryEntity> historyEntry =
        viewedHistoryJpaAdapter.findById(new ViewedHistoryId(testAccountId, productId));

    assertThat(historyEntry).isPresent();
    assertThat(historyEntry.get().getId().getAccountId()).isEqualTo(testAccountId);
    assertThat(historyEntry.get().getId().getProductId()).isEqualTo(productId);

    // Clean up
    viewedHistoryRepository.deleteViewedProduct(testAccountId, productId);
    productJpaAdapter.deleteById(productId);
  }

  @Test
  void getViewedProductsReturnsExpectedProductsTest() {
    // Given
    UUID productId = createTestProduct();
    Long testAccountId = getUserId();
    viewedHistoryRepository.addOrUpdateViewedProduct(testAccountId, productId);
    Pageable pageable = Pageable.builder().page(0).size(10).sort(List.of("viewedAt,DESC")).build();

    // When
    Page<Product> result = viewedHistoryRepository.getViewedProducts(testAccountId, pageable, TEST_LANGUAGE);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.content()).isNotEmpty();
    assertThat(result.content().stream().anyMatch(p -> p.getId().equals(productId))).isTrue();

    // Clean up
    viewedHistoryRepository.deleteViewedProduct(testAccountId, productId);
    productJpaAdapter.deleteById(productId);
  }

  @Test
  void deleteViewedProductRemovesProductTest() {
    // Given
    UUID productId = createTestProduct();
    Long testAccountId = getUserId();
    viewedHistoryRepository.addOrUpdateViewedProduct(testAccountId, productId);

    // When
    viewedHistoryRepository.deleteViewedProduct(testAccountId, productId);

    // Then
    Optional<ViewedHistoryEntity> historyEntry =
        viewedHistoryJpaAdapter.findById(new ViewedHistoryId(testAccountId, productId));
    assertThat(historyEntry).isEmpty();
  }

  @Test
  void deleteAllViewedProductsRemovesAllForAccountTest() {
    // Given
    UUID productId1 = createTestProduct();
    UUID productId2 = createTestProduct();
    Long testAccountId = getUserId();
    viewedHistoryRepository.addOrUpdateViewedProduct(testAccountId, productId1);
    viewedHistoryRepository.addOrUpdateViewedProduct(testAccountId, productId2);

    // When
    viewedHistoryRepository.deleteAllViewedProducts(testAccountId);
    entityManager.flush();
    entityManager.clear();

    // Then
    assertThat(viewedHistoryJpaAdapter.findById(new ViewedHistoryId(testAccountId, productId1))).isEmpty();
    assertThat(viewedHistoryJpaAdapter.findById(new ViewedHistoryId(testAccountId, productId2))).isEmpty();
  }

  // helper
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
    return finalSavedProduct.getId();
  }

  private Long getUserId() {
    return accountJpaAdapter.findByEmail(user).get().getId();
  }
}
