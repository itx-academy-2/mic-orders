package com.academy.orders.domain.reservation.repository;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.reservation.entity.ProductReservationDetails;
import java.util.UUID;

/**
 * Repository interface for management-oriented reservation queries.
 */
public interface ReservationManagementRepository {

  /**
   * Returns a page of reservation records for a given product.
   *
   * @param productId product identifier
   * @param pageable paging information
   * @return page of {@link ProductReservationDetails}
   */
  Page<ProductReservationDetails> getProductReservations(UUID productId, Pageable pageable);
}
