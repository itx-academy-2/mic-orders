package com.academy.orders.application.reservation.usecase;

import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.ChangeQuantityUseCase;
import com.academy.orders.domain.reservation.repository.ReservationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;

import static com.academy.orders.application.TestConstants.TEST_ID;
import static com.academy.orders.application.TestConstants.TEST_UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecreaseProductReservationQuantityUseCaseImplTest {

  private static final Long TEST_USER_ID = TEST_ID;

  private static final UUID TEST_PRODUCT_ID = TEST_UUID;

  @InjectMocks
  private DecreaseProductReservationQuantityUseCaseImpl useCase;

  @Mock
  private ReservationsRepository reservationsRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ChangeQuantityUseCase changeQuantityUseCase;

  @Test
  void decreaseReservationQuantityWhenNothingReservedTest() {
    // Given
    when(reservationsRepository.getReservedQuantity(TEST_USER_ID, TEST_PRODUCT_ID)).thenReturn(0);

    // When
    useCase.decreaseReservationQuantity(TEST_USER_ID, TEST_PRODUCT_ID);

    // Then
    verify(reservationsRepository, times(1)).getReservedQuantity(TEST_USER_ID, TEST_PRODUCT_ID);
    verifyNoInteractions(productRepository, changeQuantityUseCase);
  }

  @Test
  void decreaseReservationQuantityWhenProductNotFoundTest() {
    // Given
    when(reservationsRepository.getReservedQuantity(TEST_USER_ID, TEST_PRODUCT_ID)).thenReturn(2);
    when(productRepository.getById(TEST_PRODUCT_ID)).thenReturn(Optional.empty());

    // When
    useCase.decreaseReservationQuantity(TEST_USER_ID, TEST_PRODUCT_ID);

    // Then
    verify(reservationsRepository, times(1)).removeProductFromReservations(TEST_USER_ID, TEST_PRODUCT_ID);
    verify(changeQuantityUseCase, never()).changeQuantityOfProduct(any(), anyInt());
  }

  @Test
  void decreaseReservationQuantityWhenQuantityGreaterThanOneTest() {
    // Given
    Product product = new Product();
    when(reservationsRepository.getReservedQuantity(TEST_USER_ID, TEST_PRODUCT_ID)).thenReturn(3);
    when(productRepository.getById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));

    // When
    useCase.decreaseReservationQuantity(TEST_USER_ID, TEST_PRODUCT_ID);

    // Then
    verify(changeQuantityUseCase, times(1)).changeQuantityOfProduct(product, -1);
    verify(reservationsRepository, times(1)).decrementProductReservationQuantity(TEST_USER_ID, TEST_PRODUCT_ID);
    verify(reservationsRepository, never()).removeProductFromReservations(any(), any());
  }

  @Test
  void decreaseReservationQuantityWhenQuantityEqualsOneTest() {
    // Given
    Product product = new Product();
    when(reservationsRepository.getReservedQuantity(TEST_USER_ID, TEST_PRODUCT_ID)).thenReturn(1);
    when(productRepository.getById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));

    // When
    useCase.decreaseReservationQuantity(TEST_USER_ID, TEST_PRODUCT_ID);

    // Then
    verify(changeQuantityUseCase, times(1)).changeQuantityOfProduct(product, -1);
    verify(reservationsRepository, times(1)).removeProductFromReservations(TEST_USER_ID, TEST_PRODUCT_ID);
    verify(reservationsRepository, never()).decrementProductReservationQuantity(any(), any());
  }
}
