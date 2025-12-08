package com.academy.orders.infrastructure.reservation.repository;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.reservation.entity.ProductReservationDetails;
import com.academy.orders.domain.reservation.repository.ReservationManagementRepository;
import com.academy.orders.infrastructure.common.PageableMapper;
import com.academy.orders.infrastructure.reservation.mapper.ReservationPageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationManagementRepositoryImpl implements ReservationManagementRepository {

  private final ReservationsManagementJpaAdapter reservationsManagementJpa;

  private final PageableMapper pageableMapper;

  private final ReservationPageMapper reservationPageMapper;

  @Override
  public Page<ProductReservationDetails> getProductReservations(UUID productId, Pageable pageable) {
    var springPageable = pageableMapper.fromDomain(pageable);
    var result = reservationsManagementJpa.findByProductId(productId, springPageable);

    return reservationPageMapper.fromProjection(result);
  }
}
