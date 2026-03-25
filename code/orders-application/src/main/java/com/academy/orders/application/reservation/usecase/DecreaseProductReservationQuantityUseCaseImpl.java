package com.academy.orders.application.reservation.usecase;

import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.ChangeQuantityUseCase;
import com.academy.orders.domain.reservation.repository.ReservationsRepository;
import com.academy.orders.domain.reservation.usecase.DecreaseProductReservationQuantityUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecreaseProductReservationQuantityUseCaseImpl implements DecreaseProductReservationQuantityUseCase {

  private final ReservationsRepository reservationsRepository;

  private final ProductRepository productRepository;

  private final ChangeQuantityUseCase changeQuantityUseCase;

  @Override
  @Transactional
  public void decreaseReservationQuantity(Long userId, UUID productId) {

    if (!reservationsRepository.exists(userId, productId)) {
      log.info("Product {} is not reserved by user {}, skipping decrement", productId, userId);
      return;
    }

    long reservedQuantity = reservationsRepository.getReservedQuantity(userId, productId);

    var productOpt = productRepository.getById(productId);

    if (productOpt.isEmpty()) {
      log.warn("Product {} not found, removing reservation for user {}", productId, userId);
      reservationsRepository.removeProductFromReservations(userId, productId);
      return;
    }

    var product = productOpt.get();

    if (reservedQuantity > 1) {
      log.info("Decreasing reservation quantity for product {} for user {}", productId, userId);

      changeQuantityUseCase.changeQuantityOfProduct(product, 1);
      reservationsRepository.decrementProductReservationQuantity(userId, productId);

    } else {
      log.info("Last reserved item, removing product {} for user {}", productId, userId);

      changeQuantityUseCase.changeQuantityOfProduct(product, 1);
      reservationsRepository.removeProductFromReservations(userId, productId);
    }
  }
}
