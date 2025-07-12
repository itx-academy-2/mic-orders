package com.academy.orders.application.viewhistory.usecase;

import com.academy.orders.domain.viewhistory.repository.ViewedHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.academy.orders.application.TestConstants.TEST_ID;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteAllViewedProductsUseCaseImplTest {

  @InjectMocks
  private DeleteAllViewedProductsUseCaseImpl deleteAllViewedProductsUseCase;

  @Mock
  private ViewedHistoryRepository viewedHistoryRepository;

  @Test
  void deleteAllViewedProductsShouldInvokeRepositoryOnceTest() {
    // Given
    doNothing().when(viewedHistoryRepository).deleteAllViewedProducts(TEST_ID);

    // When
    deleteAllViewedProductsUseCase.deleteAllViewedProducts(TEST_ID);

    // Then
    verify(viewedHistoryRepository, times(1)).deleteAllViewedProducts(TEST_ID);
  }
}
