package com.academy.orders.infrastructure.wishlist.repository;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.infrastructure.common.PageableMapper;
import com.academy.orders.infrastructure.product.ProductMapper;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import com.academy.orders.infrastructure.wishlist.entity.WishlistId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.UUID;

import static com.academy.orders.infrastructure.ModelUtils.getPageImplOf;
import static com.academy.orders.infrastructure.ModelUtils.getPageable;
import static com.academy.orders.infrastructure.ModelUtils.getProduct;
import static com.academy.orders.infrastructure.ModelUtils.getProductTranslationEntity;
import static com.academy.orders.infrastructure.TestConstants.LANGUAGE_EN;
import static com.academy.orders.infrastructure.TestConstants.TEST_ID;
import static com.academy.orders.infrastructure.TestConstants.TEST_UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WishlistRepositoryImplTest {

  private static final Long ACCOUNT_ID = TEST_ID;

  private static final UUID PRODUCT_ID = TEST_UUID;

  private static final Pageable PAGEABLE = getPageable(0, 10, List.of("product.price,DESC"));

  private static final org.springframework.data.domain.PageRequest PAGEABLE_SPRING =
      PageRequest.of(0, 10, Sort.by(Sort.Order.desc("product.price")));

  private static final org.springframework.data.domain.PageRequest PAGEABLE_SPRING_AFTER_REMAP =
      PageRequest.of(0, 10, Sort.by(Sort.Order.desc("p.price")));

  @InjectMocks
  private WishlistRepositoryImpl repository;

  @Mock
  private WishlistJpaAdapter wishlistJpaAdapter;

  @Mock
  private WishlistProductTranslationJpaAdapter wishlistProductTranslationJpaAdapter;

  @Mock
  private PageableMapper pageableMapper;

  @Mock
  private ProductMapper productMapper;

  @Test
  void addProductToWishlistIfNotExistsTest() {
    // Given
    WishlistId wishlistId = new WishlistId(ACCOUNT_ID, PRODUCT_ID);
    when(wishlistJpaAdapter.existsById(wishlistId)).thenReturn(false);

    // When
    repository.addProductToWishlist(ACCOUNT_ID, PRODUCT_ID);

    // Then
    verify(wishlistJpaAdapter).existsById(wishlistId);
    verify(wishlistJpaAdapter).save(argThat(
        entity -> entity.getId().equals(wishlistId)));
  }

  @Test
  void addProductToWishlistIfAlreadyExistsTest() {
    // Given
    WishlistId wishlistId = new WishlistId(ACCOUNT_ID, PRODUCT_ID);
    when(wishlistJpaAdapter.existsById(wishlistId)).thenReturn(true);

    // When
    repository.addProductToWishlist(ACCOUNT_ID, PRODUCT_ID);

    // Then
    verify(wishlistJpaAdapter).existsById(wishlistId);
    verify(wishlistJpaAdapter, never()).save(any());
  }

  @Test
  void removeProductFromWishlistTest() {
    // Given
    WishlistId wishlistId = new WishlistId(ACCOUNT_ID, PRODUCT_ID);

    // When
    repository.removeProductFromWishlist(ACCOUNT_ID, PRODUCT_ID);

    // Then
    verify(wishlistJpaAdapter).deleteById(wishlistId);
  }

  @Test
  void getWishlistProductsTest() {
    // Given
    var translationEntity = getProductTranslationEntity();
    var domainProduct = getProduct();
    var pageImpl = getPageImplOf(translationEntity);
    when(pageableMapper.fromDomain(PAGEABLE)).thenReturn(PAGEABLE_SPRING);
    when(wishlistProductTranslationJpaAdapter.findWishlistProductTranslations(ACCOUNT_ID, LANGUAGE_EN, PAGEABLE_SPRING_AFTER_REMAP))
        .thenReturn(pageImpl);
    when(productMapper.fromEntity(translationEntity)).thenReturn(domainProduct);

    // When
    Page<Product> result = repository.getWishlistProducts(ACCOUNT_ID, PAGEABLE, LANGUAGE_EN);

    // Then
    assertFalse(result.empty());
    assertEquals(1, result.totalElements());
    assertEquals(domainProduct, result.content().get(0));
    verify(pageableMapper, times(1)).fromDomain(PAGEABLE);
    verify(wishlistProductTranslationJpaAdapter, times(1)).findWishlistProductTranslations(eq(ACCOUNT_ID), eq(LANGUAGE_EN),
        any(org.springframework.data.domain.Pageable.class));
    verify(productMapper, times(1)).fromEntity(translationEntity);
  }

  @Test
  void getWishlistProductsShouldReturnEmptyPageTest() {
    // Given
    var emptyPage = new PageImpl<ProductTranslationEntity>(List.of(), PAGEABLE_SPRING, 0);
    when(pageableMapper.fromDomain(PAGEABLE)).thenReturn(PAGEABLE_SPRING);
    when(wishlistProductTranslationJpaAdapter.findWishlistProductTranslations(ACCOUNT_ID, LANGUAGE_EN, PAGEABLE_SPRING_AFTER_REMAP))
        .thenReturn(emptyPage);

    // When
    Page<Product> result = repository.getWishlistProducts(ACCOUNT_ID, PAGEABLE, LANGUAGE_EN);

    // Then
    assertTrue(result.empty());
    assertEquals(0, result.totalElements());
    assertEquals(List.of(), result.content());
    verify(pageableMapper, times(1)).fromDomain(PAGEABLE);
    verify(wishlistProductTranslationJpaAdapter, times(1)).findWishlistProductTranslations(eq(ACCOUNT_ID), eq(LANGUAGE_EN),
        any(org.springframework.data.domain.Pageable.class));
    verify(productMapper, never()).fromEntity(any(ProductTranslationEntity.class));
  }

}
