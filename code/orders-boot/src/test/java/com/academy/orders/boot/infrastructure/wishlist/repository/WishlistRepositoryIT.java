package com.academy.orders.boot.infrastructure.wishlist.repository;

import com.academy.orders.boot.infrastructure.common.repository.AbstractRepositoryIT;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import com.academy.orders.domain.wishlist.repository.WishlistRepository;
import com.academy.orders.infrastructure.account.repository.AccountJpaAdapter;
import com.academy.orders.infrastructure.language.entity.LanguageEntity;
import com.academy.orders.infrastructure.language.repository.LanguageJpaAdapter;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationId;
import com.academy.orders.infrastructure.product.repository.ProductJpaAdapter;
import com.academy.orders.infrastructure.wishlist.entity.WishlistId;
import com.academy.orders.infrastructure.wishlist.repository.WishlistJpaAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static com.academy.orders.ModelUtils.getPageableSortAsc;
import static com.academy.orders.boot.TestConstants.LANGUAGE_EN;
import static org.assertj.core.api.Assertions.assertThat;

public class WishlistRepositoryIT extends AbstractRepositoryIT {

  @Value("${auth.users[0].username}")
  private String user;

  @Autowired
  private WishlistRepository wishlistRepository;

  @Autowired
  private WishlistJpaAdapter wishlistJpaAdapter;

  @Autowired
  private ProductJpaAdapter productJpaAdapter;

  @Autowired
  private LanguageJpaAdapter languageJpaAdapter;

  @Autowired
  private AccountJpaAdapter accountJpaAdapter;

  @Test
  void addProductToWishlistTest() {
    // Given
    UUID productId = createTestProduct();
    Long userId = getUserId();

    // When
    wishlistRepository.addProductToWishlist(userId, productId);

    // Then
    assertThat(wishlistJpaAdapter.findById(new WishlistId(userId, productId))).isPresent();

    // Cleanup
    wishlistRepository.removeProductFromWishlist(userId, productId);
    productJpaAdapter.deleteById(productId);
  }

  @Test
  void getWishlistProductsTest() {
    // Given
    UUID productId = createTestProduct();
    Long userId = getUserId();
    wishlistRepository.addProductToWishlist(userId, productId);
    Pageable pageable = getPageableSortAsc();

    // When
    Page<Product> result = wishlistRepository.getWishlistProducts(userId, pageable, LANGUAGE_EN);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.content()).isNotEmpty();
    assertThat(result.content().stream().anyMatch(p -> p.getId().equals(productId))).isTrue();

    // Cleanup
    wishlistRepository.removeProductFromWishlist(userId, productId);
    productJpaAdapter.deleteById(productId);
  }

  @Test
  void removeProductFromWishlistTest() {
    // Given
    UUID productId = createTestProduct();
    Long userId = getUserId();
    wishlistRepository.addProductToWishlist(userId, productId);

    // When
    wishlistRepository.removeProductFromWishlist(userId, productId);

    // Then
    assertThat(wishlistJpaAdapter.findById(new WishlistId(userId, productId))).isEmpty();

    // Cleanup
    productJpaAdapter.deleteById(productId);
  }

  @Test
  void addProductToWishlistDoesNotDuplicateEntryTest() {
    // Given
    UUID productId = createTestProduct();
    Long userId = getUserId();
    wishlistRepository.addProductToWishlist(userId, productId);
    wishlistRepository.addProductToWishlist(userId, productId);

    // When
    // Still only one
    long count = wishlistJpaAdapter.findAll().stream()
        .filter(w -> w.getId().getAccountId().equals(userId) && w.getId().getProductId().equals(productId))
        .count();

    // Then
    assertThat(count).isEqualTo(1);

    // Cleanup
    wishlistRepository.removeProductFromWishlist(userId, productId);
    productJpaAdapter.deleteById(productId);
  }

  private UUID createTestProduct() {
    LanguageEntity language = languageJpaAdapter.findByCode(LANGUAGE_EN)
        .orElseGet(() -> languageJpaAdapter.save(LanguageEntity.builder().code(LANGUAGE_EN).build()));

    ProductEntity product = ProductEntity.builder()
        .status(ProductStatus.VISIBLE)
        .image("https://example.com/test.jpg")
        .quantity(5)
        .price(BigDecimal.valueOf(999))
        .build();

    ProductEntity savedProduct = productJpaAdapter.saveAndFlush(product);

    ProductTranslationEntity translation = ProductTranslationEntity.builder()
        .productTranslationId(new ProductTranslationId(savedProduct.getId(), language.getId()))
        .name("Wishlist Test Product")
        .product(savedProduct)
        .language(language)
        .build();

    savedProduct.setProductTranslations(new HashSet<>(List.of(translation)));
    return productJpaAdapter.saveAndFlush(savedProduct).getId();
  }

  private Long getUserId() {
    return accountJpaAdapter.findByEmail(user).orElseThrow().getId();
  }
}
