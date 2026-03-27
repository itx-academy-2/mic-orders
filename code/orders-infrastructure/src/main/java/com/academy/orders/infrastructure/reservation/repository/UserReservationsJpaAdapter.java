package com.academy.orders.infrastructure.reservation.repository;

import com.academy.orders.infrastructure.reservation.entity.ReservationEntity;
import com.academy.orders.infrastructure.reservation.entity.ReservationId;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
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

  /**
   * Locks all reservations for the given user.
   *
   * @param userId the user ID
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT r FROM ReservationEntity r WHERE r.id.userId = :userId")
  List<ReservationEntity> lockAllByUserId(@Param("userId") Long userId);

  /**
   * Inserts a new reservation with quantity = 1 for the given user and product, or atomically increments the quantity if the reservation
   * already exists. This operation is performed at the database level using an upsert to ensure correctness under concurrent requests.
   */
  @Modifying
  @Query(value = """
        INSERT INTO user_reservations (user_id, product_id, quantity, added_at)
        VALUES (:userId, :productId, 1, now())
        ON CONFLICT (user_id, product_id)
        DO UPDATE SET quantity = user_reservations.quantity + 1
      """, nativeQuery = true)
  void upsertAndIncrement(@Param("userId") Long userId, @Param("productId") UUID productId);

  /**
   * Decrements quantity by 1 if it is greater than 1.
   *
   * @return number of affected rows (0 or 1)
   */
  @Modifying
  @Query("""
      UPDATE ReservationEntity r
         SET r.quantity = r.quantity - 1
       WHERE r.id.userId = :userId
         AND r.id.productId = :productId
         AND r.quantity > 1
      """)
  int decrementQuantityIfGreaterThanOne(@Param("userId") Long userId, @Param("productId") UUID productId);

  /**
   * Deletes a reservation only if its quantity equals 1.
   *
   * @return number of affected rows (0 or 1)
   */
  @Modifying
  @Query("""
      DELETE FROM ReservationEntity r
       WHERE r.id.userId = :userId
         AND r.id.productId = :productId
         AND r.quantity = 1
      """)
  int deleteIfQuantityIsOne(@Param("userId") Long userId, @Param("productId") UUID productId);
}
