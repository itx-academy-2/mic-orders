package com.academy.orders.domain.reservation.repository;

import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.reservation.entity.ReservationMetadata;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Repository interface for accessing and managing user's reservations.
 */
public interface ReservationsRepository {

  /**
   * Adds a product to the user's reservations or increases its quantity by 1 if already reserved.
   *
   * @param accountId the {@link Long} ID of the user.
   * @param productId the {@link UUID} ID of the product to add.
   */
  void addProductToReservations(Long accountId, UUID productId);

  /**
   * Removes a product from the user's reservations.
   *
   * @param accountId the {@link Long} ID of the user.
   * @param productId the {@link UUID} ID of the product to remove.
   */
  void removeProductFromReservations(Long accountId, UUID productId);

  /**
   * Decreases the reserved quantity of a product by 1 for a user. If the quantity becomes zero, the reservation is removed.
   *
   * @param accountId the user ID
   * @param productId the product ID
   */
  void decrementProductReservationQuantity(Long accountId, UUID productId);

  /**
   * Retrieves all reserved products for the user.
   *
   * @param accountId the {@link Long} ID of the user.
   * @param language the {@link String} language code.
   * @return list of {@link Product}
   */
  List<Product> getReservationProducts(Long accountId, String language);

  /**
   * Retrieves all reservation metadata for the user.
   *
   * @param accountId the {@link Long} ID of the user.
   * @return list of {@link ReservationMetadata}
   */
  List<ReservationMetadata> getUserReservationMetadata(Long accountId);

  /**
   * Returns the total quantity of all reserved items. Used to validate maximum total quantity limit.
   *
   * @param accountId the user ID
   * @return sum of reserved quantities
   */
  long sumReservedQuantity(Long accountId);

  /**
   * Calculates current total cost of all reserved products. Used to validate maximum total price rule.
   *
   * @param accountId the user ID
   * @return sum of prices as BigDecimal
   */
  BigDecimal calculateTotalReservationCost(Long accountId);

  /**
   * Checks if the given product is already reserved by user.
   *
   * @param accountId the user ID
   * @param productId the product ID
   * @return true if product is already reserved
   */
  boolean exists(Long accountId, UUID productId);

  /**
   * Returns the reserved quantity for a specific product for a user.
   *
   * @param accountId the user ID
   * @param productId the product ID
   * @return reserved quantity, 0 if not reserved
   */
  int getReservedQuantity(Long accountId, UUID productId);

  /**
   * Returns reserved quantity for given products.
   *
   * @param productIds list of product ids
   * @return map where key = productId and value = reserved quantity
   */
  Map<UUID, Long> getReservedQuantitiesByProductIds(List<UUID> productIds);
}
