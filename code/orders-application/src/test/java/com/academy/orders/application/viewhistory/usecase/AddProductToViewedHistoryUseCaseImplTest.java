package com.academy.orders.application.viewhistory.usecase;

import com.academy.orders.domain.product.exception.ProductNotFoundException;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.viewhistory.repository.ViewedHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.academy.orders.application.TestConstants.TEST_ID;
import static com.academy.orders.application.TestConstants.TEST_UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddProductToViewedHistoryUseCaseImplTest {

  @InjectMocks
  private AddProductToViewedHistoryUseCaseImpl addProductToViewedHistoryUseCase;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ViewedHistoryRepository viewedHistoryRepository;

  @Test
  void addProductToViewedHistoryWhenProductExistsTest() {
    // Given
    when(productRepository.existById(TEST_UUID)).thenReturn(true);
    doNothing().when(viewedHistoryRepository).addOrUpdateViewedProduct(TEST_ID, TEST_UUID);

    // When
    addProductToViewedHistoryUseCase.addProductToViewedHistory(TEST_ID, TEST_UUID);

    // Then
    verify(productRepository, times(1)).existById(TEST_UUID);
    verify(viewedHistoryRepository, times(1)).addOrUpdateViewedProduct(TEST_ID, TEST_UUID);
  }

  @Test
  void addProductToViewedHistoryWhenProductDoesNotExistTest() {
    // Given
    when(productRepository.existById(TEST_UUID)).thenReturn(false);

    // When / Then
    assertThrows(ProductNotFoundException.class, () -> addProductToViewedHistoryUseCase.addProductToViewedHistory(TEST_ID, TEST_UUID));

    verify(productRepository, times(1)).existById(TEST_UUID);
    verify(viewedHistoryRepository, never()).addOrUpdateViewedProduct(anyLong(), any());
  }
}
