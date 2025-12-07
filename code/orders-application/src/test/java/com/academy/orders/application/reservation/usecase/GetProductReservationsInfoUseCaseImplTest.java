package com.academy.orders.application.reservation.usecase;

import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.exception.ProductNotFoundException;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.reservation.repository.ReservationManagementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.UUID;

import static com.academy.orders.application.ModelUtils.createProductReservationDetails;
import static com.academy.orders.application.ModelUtils.getProductReservationDetailsPage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetProductReservationsInfoUseCaseImplTest {

  private final UUID TEST_PRODUCT_ID = UUID.randomUUID();

  @InjectMocks
  private GetProductReservationsInfoUseCaseImpl getProductReservationsInfoUseCase;

  @Mock
  private ReservationManagementRepository reservationManagementRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private Pageable pageable;

  @Test
  void getProductReservationsInfoWhenProductExistsTest() {
    // Given
    var reservation = createProductReservationDetails();
    var expectedPage = getProductReservationDetailsPage(List.of(reservation), 1, 10);
    when(productRepository.existById(TEST_PRODUCT_ID)).thenReturn(true);
    when(reservationManagementRepository.getProductReservations(TEST_PRODUCT_ID, pageable)).thenReturn(expectedPage);

    // When
    var result = getProductReservationsInfoUseCase.getProductReservationsInfo(TEST_PRODUCT_ID, pageable);

    // Then
    assertEquals(expectedPage, result);
    verify(productRepository, times(1)).existById(TEST_PRODUCT_ID);
    verify(reservationManagementRepository, times(1)).getProductReservations(TEST_PRODUCT_ID, pageable);
  }

  @Test
  void getProductReservationsInfoWhenProductDoesNotExistTest() {
    // Given
    when(productRepository.existById(TEST_PRODUCT_ID)).thenReturn(false);

    // When / Then
    assertThrows(ProductNotFoundException.class,
        () -> getProductReservationsInfoUseCase.getProductReservationsInfo(TEST_PRODUCT_ID, pageable));

    verify(productRepository, times(1)).existById(TEST_PRODUCT_ID);
    verifyNoInteractions(reservationManagementRepository);
  }
}
