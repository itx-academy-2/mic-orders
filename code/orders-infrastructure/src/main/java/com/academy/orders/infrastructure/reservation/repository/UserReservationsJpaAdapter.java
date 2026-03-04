package com.academy.orders.infrastructure.reservation.repository;

import com.academy.orders.infrastructure.reservation.entity.ReservationEntity;
import com.academy.orders.infrastructure.reservation.entity.ReservationId;
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
   * Checks if the given product is already reserved by the user.
   */
  boolean existsByIdUserIdAndIdProductId(Long userId, UUID productId);

  /**
   * Counts how many products the user has currently reserved.
   */
  long countByIdUserId(Long userId);

  /**
   * Computes the total price of all user's reserved products. Uses product.price directly.
   */
  @Query("""
      SELECT COALESCE(SUM(p.price), 0)
        FROM ReservationEntity r
        JOIN ProductEntity p ON p.id = r.id.productId
       WHERE r.id.userId = :userId
      """)
  BigDecimal totalCostOfReservations(@Param("userId") Long userId);

  /**
   * Retrieves all reservation records for the given user.
   */
  List<ReservationEntity> findByIdUserId(Long userId);
}
