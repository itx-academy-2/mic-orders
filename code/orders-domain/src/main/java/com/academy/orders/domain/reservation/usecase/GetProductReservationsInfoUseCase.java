package com.academy.orders.domain.reservation.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.reservation.entity.ProductReservationDetails;
import java.util.UUID;

/**
 * Use case for retrieving paginated reservation details for a product (management).
 */
public interface GetProductReservationsInfoUseCase {

  /**
   * Retrieves paginated reservation details for the provided product id.
   *
   * @param productId the product UUID.
   * @param pageable paging information
   * @return page of {@link ProductReservationDetails}
   */
  Page<ProductReservationDetails> getProductReservationsInfo(UUID productId, Pageable pageable);
}
