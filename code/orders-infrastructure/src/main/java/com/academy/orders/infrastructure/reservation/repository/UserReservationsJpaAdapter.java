package com.academy.orders.infrastructure.reservation.repository;

import com.academy.orders.infrastructure.reservation.entity.ReservationEntity;
import com.academy.orders.infrastructure.reservation.entity.ReservationId;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * JPA adapter for accessing the user_reservations table.
 *
 * This repository provides low-level DB access for reading and writing user reservation information (without applying domain rules).
 */
@Repository
public interface UserReservationsJpaAdapter extends JpaRepository<ReservationEntity, ReservationId> {

  /**
   * Returns total quantity of all reserved items for the user.
   */
  @Query("""
      SELECT COALESCE(SUM(r.quantity), 0)
      FROM ReservationEntity r
      WHERE r.id.userId = :userId
      """)
  int sumReservedQuantity(@Param("userId") Long userId);

  /**
   * Computes the total price of all user's reserved products.
   */
  @Query("""
      SELECT COALESCE(SUM(p.price * r.quantity), 0)
        FROM ReservationEntity r
        JOIN ProductEntity p ON p.id = r.id.productId
       WHERE r.id.userId = :userId
      """)
  BigDecimal totalCostOfReservations(@Param("userId") Long userId);

  /**
   * Retrieves all reservation records for the given user.
   */
  List<ReservationEntity> findByIdUserId(Long userId);

  /**
   * Returns total reserved quantity for each product.
   *
   * @param productIds list of product IDs
   * @return tuples: productId + reservedQuantity
   */
  @Query("""
      SELECT r.id.productId as productId, SUM(r.quantity) as reservedQuantity
      FROM ReservationEntity r
      WHERE r.id.productId IN :productIds
      GROUP BY r.id.productId
      """)
  List<Tuple> sumReservedProductsByProductIds(@Param("productIds") List<UUID> productIds);
}
