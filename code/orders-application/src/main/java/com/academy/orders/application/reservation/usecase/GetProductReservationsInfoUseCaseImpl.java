package com.academy.orders.application.reservation.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.exception.ProductNotFoundException;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.reservation.entity.ProductReservationDetails;
import com.academy.orders.domain.reservation.repository.ReservationManagementRepository;
import com.academy.orders.domain.reservation.usecase.GetProductReservationsInfoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetProductReservationsInfoUseCaseImpl implements GetProductReservationsInfoUseCase {

  private final ReservationManagementRepository reservationManagementRepository;

  private final ProductRepository productRepository;

  @Override
  public Page<ProductReservationDetails> getProductReservationsInfo(UUID productId, Pageable pageable) {
    log.info("Fetching reservations for product [{}] with pageable [{}]", productId, pageable);

    if (!productRepository.existById(productId)) {
      log.warn("Product [{}] not found", productId);
      throw new ProductNotFoundException(productId);
    }

    return reservationManagementRepository.getProductReservations(productId, pageable);
  }
}
