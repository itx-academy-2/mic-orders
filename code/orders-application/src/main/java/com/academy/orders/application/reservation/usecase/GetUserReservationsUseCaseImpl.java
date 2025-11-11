package com.academy.orders.application.reservation.usecase;

import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.reservation.repository.ReservationsRepository;
import com.academy.orders.domain.reservation.usecase.GetUserReservationsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetUserReservationsUseCaseImpl implements GetUserReservationsUseCase {

  private final ReservationsRepository reservationsRepository;

  @Override
  @Transactional(readOnly = true)
  public List<Product> getUserReservations(Long userId, String language) {
    log.info("Fetching reserved products for user [{}] with language [{}]", userId, language);
    return reservationsRepository.getReservationProducts(userId, language);
  }
}
