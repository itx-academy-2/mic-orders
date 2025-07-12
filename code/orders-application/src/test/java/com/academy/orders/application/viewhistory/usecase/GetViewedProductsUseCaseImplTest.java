package com.academy.orders.application.viewhistory.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.viewhistory.repository.ViewedHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static com.academy.orders.application.ModelUtils.getPage;
import static com.academy.orders.application.ModelUtils.getPageable;
import static com.academy.orders.application.TestConstants.LANGUAGE_EN;
import static com.academy.orders.application.TestConstants.TEST_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetViewedProductsUseCaseImplTest {

  @Mock
  private ViewedHistoryRepository viewedHistoryRepository;

  @InjectMocks
  private GetViewedProductsUseCaseImpl getViewedProductsUseCase;

  @Test
  void getViewedProductsWithNoSortShouldApplyDefaultSortTest() {
    // Given
    Pageable unsortedPageable = getPageable(0, 10, List.of());
    Pageable defaultSortedPageable = getPageable(0, 10, List.of("viewedAt,DESC"));
    Page<Product> expectedPage = getPage(List.of(), 0L, 1, 0, 10);
    when(viewedHistoryRepository.getViewedProducts(TEST_ID, defaultSortedPageable, LANGUAGE_EN)).thenReturn(expectedPage);

    // When
    Page<Product> result = getViewedProductsUseCase.getViewedProducts(TEST_ID, unsortedPageable, LANGUAGE_EN);

    // Then
    assertEquals(expectedPage, result);
    verify(viewedHistoryRepository, times(1)).getViewedProducts(TEST_ID, defaultSortedPageable, LANGUAGE_EN);
  }

  @Test
  void getViewedProductsWithCustomSortShouldNotOverrideItTest() {
    // Given
    Pageable customPageable = getPageable(1, 5, List.of("viewedAt,ASC"));
    Page<Product> expectedPage = getPage(List.of(), 0L, 1, 1, 5);
    when(viewedHistoryRepository.getViewedProducts(TEST_ID, customPageable, LANGUAGE_EN)).thenReturn(expectedPage);

    // When
    Page<Product> result = getViewedProductsUseCase.getViewedProducts(TEST_ID, customPageable, LANGUAGE_EN);

    // Then
    assertEquals(expectedPage, result);
    verify(viewedHistoryRepository, times(1)).getViewedProducts(TEST_ID, customPageable, LANGUAGE_EN);
  }

  @Test
  void getViewedProductsWithNullSortShouldApplyDefaultSortTest() {
    // Given
    Pageable nullSortPageable = Pageable.builder().page(0).size(10).sort(null).build();
    Pageable defaultSortedPageable = getPageable(0, 10, List.of("viewedAt,DESC"));
    Page<Product> expectedPage = getPage(List.of(), 0L, 1, 0, 10);
    when(viewedHistoryRepository.getViewedProducts(TEST_ID, defaultSortedPageable, LANGUAGE_EN)).thenReturn(expectedPage);

    // When
    Page<Product> result = getViewedProductsUseCase.getViewedProducts(TEST_ID, nullSortPageable, LANGUAGE_EN);

    // Then
    assertEquals(expectedPage, result);
    verify(viewedHistoryRepository, times(1)).getViewedProducts(TEST_ID, defaultSortedPageable, LANGUAGE_EN);
  }
}
