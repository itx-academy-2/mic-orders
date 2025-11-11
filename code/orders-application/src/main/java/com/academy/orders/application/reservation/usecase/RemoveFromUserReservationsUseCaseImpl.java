package com.academy.orders.application.reservation.usecase;

import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.ChangeQuantityUseCase;
import com.academy.orders.domain.reservation.repository.ReservationsRepository;
import com.academy.orders.domain.reservation.usecase.RemoveFromUserReservationsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoveFromUserReservationsUseCaseImpl implements RemoveFromUserReservationsUseCase {

  private final ReservationsRepository reservationsRepository;

  private final ProductRepository productRepository;

  private final ChangeQuantityUseCase changeQuantityUseCase;

  @Override
  @Transactional
  public void removeProductFromReservations(Long userId, UUID productId) {
    if (!reservationsRepository.exists(userId, productId)) {
      log.info("Product {} is not reserved by user {}, skipping remove", productId, userId);
      return;
    }

    var productOpt = productRepository.getById(productId);

    if (productOpt.isEmpty()) {
      log.warn("Product {} not found in catalog, removing orphaned reservation for user {}", productId, userId);
      reservationsRepository.removeProductFromReservations(userId, productId);
      return;
    }

    var product = productOpt.get();
    changeQuantityUseCase.changeQuantityOfProduct(product, -1);
    reservationsRepository.removeProductFromReservations(userId, productId);
    log.info("Product {} removed from reservations for user {}", productId, userId);
  }

}
