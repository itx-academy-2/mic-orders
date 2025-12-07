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
     * Retrieves paginated reservation details for a specific product.
     *
     * <p>This query returns a projection containing:
     * <ul>
     *   <li>the reserving user's full name (`username`)</li>
     *   <li>the user's email</li>
     *   <li>the timestamp when the reservation was created (`addedAt`)</li>
     *   <li>the `quantity` value, which is <b>not</b> stored in the database but
     *       provided as a constant (default method returning {@code 1}) in the
     *       {@link ReservationWithUserProjection} interface</li>
     * </ul>
     *
     * <p>The `quantity` field is included for domain consistency and potential
     * future extensibility, but it is not selected from the database in this query.
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
