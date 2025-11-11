package com.academy.orders.application.reservation.usecase;

import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.exception.ProductNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveFromUserReservationsUseCaseImplTest {

  private static final Long TEST_USER_ID = TEST_ID;

  private static final UUID TEST_PRODUCT_ID = TEST_UUID;

  @InjectMocks
  private RemoveFromUserReservationsUseCaseImpl removeFromUserReservationsUseCase;

  @Mock
  private ReservationsRepository reservationsRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ChangeQuantityUseCase changeQuantityUseCase;

  @Test
  void removeProductFromReservationsWhenProductExistsTest() {
    // Given
    var product = new Product();
    when(reservationsRepository.exists(TEST_USER_ID, TEST_PRODUCT_ID)).thenReturn(true);
    when(productRepository.getById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));
    doNothing().when(changeQuantityUseCase).changeQuantityOfProduct(product, -1);
    doNothing().when(reservationsRepository).removeProductFromReservations(TEST_USER_ID, TEST_PRODUCT_ID);

    // When
    removeFromUserReservationsUseCase.removeProductFromReservations(TEST_USER_ID, TEST_PRODUCT_ID);

    // Then
    verify(reservationsRepository, times(1)).exists(TEST_USER_ID, TEST_PRODUCT_ID);
    verify(productRepository, times(1)).getById(TEST_PRODUCT_ID);
    verify(changeQuantityUseCase, times(1)).changeQuantityOfProduct(product, -1);
    verify(reservationsRepository, times(1)).removeProductFromReservations(TEST_USER_ID, TEST_PRODUCT_ID);
  }

  @Test
  void removeProductFromReservationsWhenProductNotReservedTest() {
    // Given
    when(reservationsRepository.exists(TEST_USER_ID, TEST_PRODUCT_ID)).thenReturn(false);

    // When
    removeFromUserReservationsUseCase.removeProductFromReservations(TEST_USER_ID, TEST_PRODUCT_ID);

    // Then
    verify(reservationsRepository, times(1)).exists(TEST_USER_ID, TEST_PRODUCT_ID);
    verifyNoInteractions(productRepository, changeQuantityUseCase);
  }

  @Test
  void removeProductFromReservationsWhenProductNotFoundTest() {
    // Given
    when(reservationsRepository.exists(TEST_USER_ID, TEST_PRODUCT_ID)).thenReturn(true);
    when(productRepository.getById(TEST_PRODUCT_ID)).thenReturn(Optional.empty());

    // When
    assertThrows(ProductNotFoundException.class,
        () -> removeFromUserReservationsUseCase.removeProductFromReservations(TEST_USER_ID, TEST_PRODUCT_ID));

    // Then
    verify(reservationsRepository, times(1)).exists(TEST_USER_ID, TEST_PRODUCT_ID);
    verify(productRepository, times(1)).getById(TEST_PRODUCT_ID);
    verify(changeQuantityUseCase, never()).changeQuantityOfProduct(any(), anyInt());
    verify(reservationsRepository, never()).removeProductFromReservations(any(), any());
  }
}
