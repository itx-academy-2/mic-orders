package com.academy.orders.infrastructure.wishlist.repository;

import com.academy.orders.domain.wishlist.exception.UnsupportedSortFieldException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WishlistProductTranslationJpaAdapterTest {

  @Test
  void shouldRemapProductPriceSortTest() {
    // Given
    Pageable input = PageRequest.of(1, 20, Sort.by(Sort.Order.asc("product.price")));

    // When
    Pageable result = WishlistProductTranslationJpaAdapter.remapSort(input);

    // Then
    Sort sort = result.getSort();
    assertFalse(sort.isUnsorted());
    assertEquals(1, result.getPageNumber());
    assertEquals(20, result.getPageSize());

    // Because it's an unsafe sort, getOrderFor won't work as expected
    Sort.Order order = sort.stream().findFirst().orElseThrow();
    assertEquals(Sort.Direction.ASC, order.getDirection());

    // Validate that the property contains the expected raw SQL fragment
    String property = order.getProperty();
    assertTrue(property.contains("p.price"));
    assertTrue(property.contains("COALESCE"));
  }

  @Test
  void shouldRemapNameSortTest() {
    // Given
    Pageable input = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("name")));

    // When
    Pageable result = WishlistProductTranslationJpaAdapter.remapSort(input);

    // Then
    Sort.Order remappedOrder = result.getSort().getOrderFor("pt.name");
    assertNotNull(remappedOrder);
    assertEquals(Sort.Direction.DESC, remappedOrder.getDirection());
    assertEquals(0, result.getPageNumber());
    assertEquals(10, result.getPageSize());
  }

  @Test
  void shouldRemapAddedAtSortTest() {
    // Given
    Pageable input = PageRequest.of(2, 5, Sort.by(Sort.Order.asc("addedAt")));

    // When
    Pageable result = WishlistProductTranslationJpaAdapter.remapSort(input);

    // Then
    Sort.Order remappedOrder = result.getSort().getOrderFor("w.addedAt");
    assertNotNull(remappedOrder);
    assertEquals(Sort.Direction.ASC, remappedOrder.getDirection());
    assertEquals(2, result.getPageNumber());
    assertEquals(5, result.getPageSize());
  }

  @Test
  void shouldThrowExceptionForUnsupportedSortFieldTest() {
    // Given
    Pageable input = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("unknown")));

    // When & Then
    UnsupportedSortFieldException exception = assertThrows(
        UnsupportedSortFieldException.class,
        () -> WishlistProductTranslationJpaAdapter.remapSort(input));
    assertEquals("Unsupported sort field: unknown", exception.getMessage());
  }
}
