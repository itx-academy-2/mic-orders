package com.academy.orders.application.reservation.usecase;

import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.reservation.repository.ReservationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static com.academy.orders.application.TestConstants.TEST_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserReservationsUseCaseImplTest {

  private static final Long TEST_USER_ID = TEST_ID;

  private static final String TEST_LANGUAGE = "en";

  @InjectMocks
  private GetUserReservationsUseCaseImpl getUserReservationsUseCase;

  @Mock
  private ReservationsRepository reservationsRepository;

  @Test
  void getUserReservationsWhenProductsExistTest() {
    // Given
    var product1 = new Product();
    var product2 = new Product();
    var expectedProducts = List.of(product1, product2);
    when(reservationsRepository.getReservationProducts(TEST_USER_ID, TEST_LANGUAGE)).thenReturn(expectedProducts);

    // When
    var result = getUserReservationsUseCase.getUserReservations(TEST_USER_ID, TEST_LANGUAGE);

    // Then
    assertEquals(expectedProducts, result);
    verify(reservationsRepository, times(1)).getReservationProducts(TEST_USER_ID, TEST_LANGUAGE);
  }

  @Test
  void getUserReservationsWhenNoProductsTest() {
    // Given
    when(reservationsRepository.getReservationProducts(TEST_USER_ID, TEST_LANGUAGE)).thenReturn(List.of());

    // When
    var result = getUserReservationsUseCase.getUserReservations(TEST_USER_ID, TEST_LANGUAGE);

    // Then
    assertEquals(0, result.size());
    verify(reservationsRepository, times(1)).getReservationProducts(TEST_USER_ID, TEST_LANGUAGE);
  }
}
