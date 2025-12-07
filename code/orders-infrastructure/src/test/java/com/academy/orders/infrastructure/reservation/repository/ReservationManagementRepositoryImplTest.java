package com.academy.orders.infrastructure.reservation.repository;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.infrastructure.common.PageableMapper;
import com.academy.orders.infrastructure.reservation.mapper.ReservationPageMapper;
import com.academy.orders.infrastructure.reservation.entity.projection.ReservationWithUserProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.UUID;

import static com.academy.orders.infrastructure.ModelUtils.createProductReservationDetails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationManagementRepositoryImplTest {
  private static final UUID TEST_PRODUCT_ID = UUID.randomUUID();

  @InjectMocks
  private ReservationManagementRepositoryImpl repository;

  @Mock
  private ReservationsManagementJpaAdapter reservationsManagementJpa;

  @Mock
  private PageableMapper pageableMapper;

  @Mock
  private ReservationPageMapper reservationPageMapper;

  @Mock
  private Pageable domainPageable;

  @Test
  void getProductReservationsTest() {
    // Given
    var reservation = createProductReservationDetails();
    var domainPage = new Page<>(2L, 1, true, true, 0, 2, 10, false, List.of(reservation));
    var projection = mock(ReservationWithUserProjection.class);
    var springPageable = PageRequest.of(0, 10);
    var springPage = new PageImpl<>(List.of(projection), springPageable, 2);
    when(pageableMapper.fromDomain(domainPageable)).thenReturn(springPageable);
    when(reservationsManagementJpa.findByProductId(TEST_PRODUCT_ID, springPageable)).thenReturn(springPage);
    when(reservationPageMapper.fromProjection(springPage)).thenReturn(domainPage);

    // When
    var result = repository.getProductReservations(TEST_PRODUCT_ID, domainPageable);

    // Then
    assertEquals(domainPage, result);
    verify(pageableMapper, times(1)).fromDomain(domainPageable);
    verify(reservationsManagementJpa, times(1)).findByProductId(TEST_PRODUCT_ID, springPageable);
    verify(reservationPageMapper, times(1)).fromProjection(springPage);
  }
}
