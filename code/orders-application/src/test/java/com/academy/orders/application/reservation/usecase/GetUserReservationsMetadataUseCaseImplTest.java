package com.academy.orders.application.reservation.usecase;

import com.academy.orders.application.reservation.usecase.config.ReservationProperties;
import com.academy.orders.domain.reservation.entity.ReservationMetadata;
import com.academy.orders.domain.reservation.repository.ReservationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserReservationsMetadataUseCaseImplTest {

  private static final Long TEST_USER_ID = 1L;

  @InjectMocks
  private GetUserReservationsMetadataUseCaseImpl useCase;

  @Mock
  private ReservationsRepository reservationsRepository;

  @Mock
  private ReservationProperties reservationProperties;

  @Test
  void getUserReservationsMetadataNormalCaseTest() {
    // Given
    UUID reservationId = UUID.randomUUID();
    Instant reservedAt = Instant.parse("2025-03-03T10:15:30Z");
    var reservation = new ReservationMetadata(reservationId, reservedAt, 2);

    when(reservationsRepository.getUserReservationMetadata(TEST_USER_ID)).thenReturn(List.of(reservation));
    when(reservationsRepository.sumReservedQuantity(TEST_USER_ID)).thenReturn(2L);
    when(reservationsRepository.calculateTotalReservationCost(TEST_USER_ID)).thenReturn(BigDecimal.valueOf(300));
    when(reservationProperties.getMaxReservedProducts()).thenReturn(5);
    when(reservationProperties.getMaxTotalCost()).thenReturn(BigDecimal.valueOf(1000));

    // When
    var result = useCase.getUserReservationsMetadata(TEST_USER_ID);

    // Then
    assertEquals(3, result.remainingItems()); // 5 - 2
    assertEquals(BigDecimal.valueOf(700), result.remainingMoney()); // 1000 - 300
    assertEquals(1, result.reservations().size());
    assertEquals(reservationId, result.reservations().get(0).productId());
    assertEquals(reservedAt, result.reservations().get(0).reservedAt());
    assertEquals(2, result.reservations().get(0).reservedQuantity());

    verify(reservationsRepository, times(1)).getUserReservationMetadata(TEST_USER_ID);
    verify(reservationsRepository, times(1)).sumReservedQuantity(TEST_USER_ID);
    verify(reservationsRepository, times(1)).calculateTotalReservationCost(TEST_USER_ID);
  }

  @Test
  void getUserReservationsMetadataWhenReservedQuantityExceedsLimitTest() {
    // Given
    when(reservationsRepository.getUserReservationMetadata(TEST_USER_ID)).thenReturn(List.of());
    when(reservationsRepository.sumReservedQuantity(TEST_USER_ID)).thenReturn(10L);
    when(reservationsRepository.calculateTotalReservationCost(TEST_USER_ID)).thenReturn(BigDecimal.ZERO);
    when(reservationProperties.getMaxReservedProducts()).thenReturn(5);
    when(reservationProperties.getMaxTotalCost()).thenReturn(BigDecimal.valueOf(1000));

    // When
    var result = useCase.getUserReservationsMetadata(TEST_USER_ID);

    // Then
    assertEquals(0, result.remainingItems());
  }

  @Test
  void getUserReservationsMetadataWhenReservedMoneyExceedsLimitTest() {
    // Given
    when(reservationsRepository.getUserReservationMetadata(TEST_USER_ID)).thenReturn(List.of());
    when(reservationsRepository.sumReservedQuantity(TEST_USER_ID)).thenReturn(1L);
    when(reservationsRepository.calculateTotalReservationCost(TEST_USER_ID)).thenReturn(BigDecimal.valueOf(2000));
    when(reservationProperties.getMaxReservedProducts()).thenReturn(5);
    when(reservationProperties.getMaxTotalCost()).thenReturn(BigDecimal.valueOf(1000));

    // When
    var result = useCase.getUserReservationsMetadata(TEST_USER_ID);

    // Then
    assertEquals(BigDecimal.ZERO, result.remainingMoney());
  }
}
