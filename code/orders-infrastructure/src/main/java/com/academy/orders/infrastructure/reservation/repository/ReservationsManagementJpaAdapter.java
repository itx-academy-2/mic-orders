package com.academy.orders.infrastructure.reservation.repository;

import com.academy.orders.infrastructure.reservation.entity.ReservationEntity;
import com.academy.orders.infrastructure.reservation.entity.ReservationId;
import com.academy.orders.infrastructure.reservation.entity.projection.ReservationWithUserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * JPA adapter for management-side reservation queries.
 *
 * This repository provides low-level DB access for retrieving detailed reservation information for a specific product, including user info
 * and reservation timestamp.
 */
@Repository
public interface ReservationsManagementJpaAdapter extends JpaRepository<ReservationEntity, ReservationId> {

  /**
   * Retrieves paginated reservation details for a specific product. This uses a projection to return: - username of the reserving user -
   * email of the reserving user - quantity (always 1 in your schema, but included for future extensibility) - timestamp when reservation
   * was added
   */
  @Query(value = """
      SELECT CONCAT(a.firstName, ' ', a.lastName) AS username, a.email AS email, r.addedAt AS addedAt
      FROM ReservationEntity r
      JOIN AccountEntity a ON a.id = r.id.userId
      WHERE r.id.productId = :productId
      """,
      countQuery = "SELECT COUNT(r) FROM ReservationEntity r WHERE r.id.productId = :productId")
  Page<ReservationWithUserProjection> findByProductId(@Param("productId") UUID productId, Pageable pageable);
}
