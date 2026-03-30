package com.academy.orders.application.reservation.usecase;

import com.academy.orders.application.reservation.usecase.config.ReservationProperties;
import com.academy.orders.domain.reservation.entity.ReservationMetadata;
import com.academy.orders.domain.reservation.entity.UserReservationsMetadata;
import com.academy.orders.domain.reservation.repository.ReservationsRepository;
import com.academy.orders.domain.reservation.usecase.GetUserReservationsMetadataUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserReservationsMetadataUseCaseImpl implements GetUserReservationsMetadataUseCase {

  private final ReservationsRepository reservationsRepository;

  private final ReservationProperties reservationProperties;

  @Override
  public UserReservationsMetadata getUserReservationsMetadata(Long userId) {

    var reservations = reservationsRepository.getUserReservationMetadata(userId);

    int maxReservedItems = reservationProperties.getMaxReservedProducts();
    BigDecimal maxReservedMoney = reservationProperties.getMaxTotalCost();

    long reservedQuantity = reservationsRepository.sumReservedQuantity(userId);
    int remainingItems = Math.toIntExact(Math.max(maxReservedItems - reservedQuantity, 0));

    BigDecimal reservedTotal = reservationsRepository.calculateTotalReservationCost(userId);
    BigDecimal remainingMoney = maxReservedMoney.subtract(reservedTotal);

    if (remainingMoney.compareTo(BigDecimal.ZERO) < 0) {
      remainingMoney = BigDecimal.ZERO;
    }

    return new UserReservationsMetadata(remainingMoney, remainingItems, reservations);
  }
}
