package com.academy.orders.application.viewhistory.usecase;

import com.academy.orders.domain.viewhistory.repository.ViewedHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.academy.orders.application.TestConstants.TEST_ID;
import static com.academy.orders.application.TestConstants.TEST_UUID;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteProductFromViewedHistoryUseCaseImplTest {

  @InjectMocks
  private DeleteProductFromViewedHistoryUseCaseImpl deleteProductFromViewedHistoryUseCase;

  @Mock
  private ViewedHistoryRepository viewedHistoryRepository;

  @Test
  void deleteProductFromViewedHistoryShouldInvokeRepositoryOnceTest() {
    // Given
    doNothing().when(viewedHistoryRepository).deleteViewedProduct(TEST_ID, TEST_UUID);

    // When
    deleteProductFromViewedHistoryUseCase.deleteProductFromViewedHistory(TEST_ID, TEST_UUID);

    // Then
    verify(viewedHistoryRepository, times(1)).deleteViewedProduct(TEST_ID, TEST_UUID);
  }
}
