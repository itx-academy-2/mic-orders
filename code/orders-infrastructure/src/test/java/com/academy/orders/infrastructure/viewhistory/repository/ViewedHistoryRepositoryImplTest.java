package com.academy.orders.infrastructure.viewhistory.repository;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.infrastructure.common.PageableMapper;
import com.academy.orders.infrastructure.product.ProductMapper;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.repository.ProductJpaAdapter;
import com.academy.orders.infrastructure.viewhistory.entity.ViewedHistoryEntity;
import com.academy.orders.infrastructure.viewhistory.entity.ViewedHistoryId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.academy.orders.infrastructure.ModelUtils.getPageable;
import static com.academy.orders.infrastructure.TestConstants.LANGUAGE_EN;
import static com.academy.orders.infrastructure.TestConstants.TEST_ID;
import static com.academy.orders.infrastructure.TestConstants.TEST_UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ViewedHistoryRepositoryImplTest {
  private static final Long ACCOUNT_ID = TEST_ID;

  private static final UUID PRODUCT_ID = TEST_UUID;

  private static final Pageable PAGEABLE = getPageable(0, 10, List.of("viewedAt,DESC"));

  @InjectMocks
  private ViewedHistoryRepositoryImpl repository;

  @Mock
  private ViewedHistoryJpaAdapter viewedHistoryJpaAdapter;

  @Mock
  private ProductJpaAdapter productJpaAdapter;

  @Mock
  private PageableMapper pageableMapper;

  @Mock
  private ProductMapper productMapper;

  @Test
  void addOrUpdateViewedProductShouldCreateNewEntryIfNotExists() {
    // Given
    when(viewedHistoryJpaAdapter.findById(any())).thenReturn(Optional.empty());
    var id = new ViewedHistoryId(ACCOUNT_ID, PRODUCT_ID);

    // When
    repository.addOrUpdateViewedProduct(ACCOUNT_ID, PRODUCT_ID);

    // Then
    verify(viewedHistoryJpaAdapter, times(1)).findById(id);
    verify(viewedHistoryJpaAdapter, times(1)).save(argThat(entity -> entity.getId().getAccountId().equals(ACCOUNT_ID)
        &&
        entity.getId().getProductId().equals(PRODUCT_ID)));
  }

  @Test
  void addOrUpdateViewedProductShouldUpdateIfExists() {
    // Given
    var existing = new ViewedHistoryEntity(ACCOUNT_ID, PRODUCT_ID);
    when(viewedHistoryJpaAdapter.findById(any())).thenReturn(Optional.of(existing));

    // When
    repository.addOrUpdateViewedProduct(ACCOUNT_ID, PRODUCT_ID);

    // Then
    verify(viewedHistoryJpaAdapter, times(1)).findById(existing.getId());
    verify(viewedHistoryJpaAdapter, times(1)).save(existing);
  }

  @Test
  void deleteAllViewedProductsShouldCallAdapter() {
    // When
    repository.deleteAllViewedProducts(ACCOUNT_ID);

    // Then
    verify(viewedHistoryJpaAdapter, times(1)).deleteAllByAccountId(ACCOUNT_ID);
  }

  @Test
  void deleteViewedProductShouldCallAdapter() {
    // When
    repository.deleteViewedProduct(ACCOUNT_ID, PRODUCT_ID);

    // Then
    verify(viewedHistoryJpaAdapter, times(1)).deleteById(new ViewedHistoryId(ACCOUNT_ID, PRODUCT_ID));
  }

  @Test
  void getViewedProductsShouldReturnPageOfProducts() {
    // Given
    var pageableSpring = PageRequest.of(0, 10);
    var viewedEntity = new ViewedHistoryEntity(ACCOUNT_ID, PRODUCT_ID);
    var historyPage = new PageImpl<>(List.of(viewedEntity), pageableSpring, 1);
    var productEntity = ProductEntity.builder().id(PRODUCT_ID).build();
    var domainProduct = Product.builder().id(PRODUCT_ID).build();
    when(pageableMapper.fromDomain(PAGEABLE)).thenReturn(pageableSpring);
    when(viewedHistoryJpaAdapter.findAllByIdAccountId(ACCOUNT_ID, pageableSpring)).thenReturn(historyPage);
    when(productJpaAdapter.findVisibleProductsByIdsAndLanguage(List.of(PRODUCT_ID), LANGUAGE_EN)).thenReturn(List.of(productEntity));
    when(productMapper.fromEntity(productEntity)).thenReturn(domainProduct);

    // When
    Page<Product> result = repository.getViewedProducts(ACCOUNT_ID, PAGEABLE, LANGUAGE_EN);

    // Then
    assertFalse(result.empty());
    assertEquals(1, result.totalElements());
    assertEquals(domainProduct, result.content().get(0));
  }

  @Test
  void getViewedProductsShouldReturnEmptyPage() {
    // Given
    var pageableSpring = PageRequest.of(0, 10);
    var emptyPage = new PageImpl<ViewedHistoryEntity>(List.of(), pageableSpring, 0);
    when(pageableMapper.fromDomain(PAGEABLE)).thenReturn(pageableSpring);
    when(viewedHistoryJpaAdapter.findAllByIdAccountId(ACCOUNT_ID, pageableSpring)).thenReturn(emptyPage);

    // When
    Page<Product> result = repository.getViewedProducts(ACCOUNT_ID, PAGEABLE, LANGUAGE_EN);

    // Then
    assertTrue(result.empty());
    assertEquals(0, result.totalElements());
    assertEquals(List.of(), result.content());
  }
}
