package com.academy.orders.application.reservation.usecase;

import com.academy.orders.application.reservation.usecase.config.ReservationProperties;
import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import com.academy.orders.domain.product.exception.ProductNotFoundException;
import com.academy.orders.domain.product.exception.ProductNotVisibleException;
import com.academy.orders.domain.product.exception.ProductOutOfStockException;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.ChangeQuantityUseCase;
import com.academy.orders.domain.reservation.exception.ReservationLimitExceededException;
import com.academy.orders.domain.reservation.exception.ReservationTotalCostExceededException;
import com.academy.orders.domain.reservation.repository.ReservationsRepository;
import com.academy.orders.domain.reservation.usecase.AddToUserReservationsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddToUserReservationsUseCaseImpl implements AddToUserReservationsUseCase {

  private final ReservationProperties reservationProperties;

  private final ProductRepository productRepository;

  private final ReservationsRepository reservationsRepository;

  private final ChangeQuantityUseCase changeQuantityUseCase;

  @Override
  @Transactional
  public void addProductToReservations(Long userId, UUID productId) {
    log.debug("Checking existence of product {} for user {}", productId, userId);

    var product = productRepository.getById(productId)
        .orElseThrow(() -> {
          log.warn("Product {} not found for user {}", productId, userId);
          return new ProductNotFoundException(productId);
        });

    if (product.getStatus() != ProductStatus.VISIBLE) {
      log.warn("Product {} is not visible for user {}", productId, userId);
      throw new ProductNotVisibleException(productId);
    }

    if (product.getQuantity() <= 0) {
      log.warn("Product {} is out of stock for user {}", productId, userId);
      throw new ProductOutOfStockException(productId);
    }

    int currentQuantity = reservationsRepository.sumReservedQuantity(userId);
    int maxReservedItems = reservationProperties.getMaxReservedProducts();

    if (currentQuantity >= maxReservedItems) {
      log.warn("User {} cannot reserve more than {} items", userId, maxReservedItems);
      throw new ReservationLimitExceededException(maxReservedItems);
    }

    BigDecimal currentTotal = reservationsRepository.calculateTotalReservationCost(userId);
    BigDecimal newTotal = currentTotal.add(product.getPrice());
    BigDecimal maxReservedMoney = reservationProperties.getMaxTotalCost();
    if (newTotal.compareTo(maxReservedMoney) > 0) {
      log.warn("User {} cannot exceed reservation cost of {}, current={} new={}",
          userId, maxReservedMoney, currentTotal, newTotal);
      throw new ReservationTotalCostExceededException(maxReservedMoney);
    }

    log.info("User {} is adding product {} to reservations", userId, productId);

    changeQuantityUseCase.changeQuantityOfProduct(product, 1);
    reservationsRepository.addProductToReservations(userId, productId);
  }
}
