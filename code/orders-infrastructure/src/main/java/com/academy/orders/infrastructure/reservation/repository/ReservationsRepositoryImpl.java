package com.academy.orders.infrastructure.reservation.repository;

import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.reservation.entity.ReservationMetadata;
import com.academy.orders.domain.reservation.repository.ReservationsRepository;
import com.academy.orders.infrastructure.product.ProductMapper;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import com.academy.orders.infrastructure.product.repository.ProductTranslationJpaAdapter;
import com.academy.orders.infrastructure.reservation.entity.ReservationEntity;
import com.academy.orders.infrastructure.reservation.entity.ReservationId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional
public class ReservationsRepositoryImpl implements ReservationsRepository {

  private final UserReservationsJpaAdapter reservationsJpa;

  private final ProductTranslationJpaAdapter productTranslationJpa;

  private final ProductMapper productMapper;

  @Override
  public void addProductToReservations(Long accountId, UUID productId) {
    ReservationId id = new ReservationId(accountId, productId);
    ReservationEntity entity = new ReservationEntity(id, Instant.now());
    reservationsJpa.save(entity);
  }

  @Override
  public void removeProductFromReservations(Long accountId, UUID productId) {
    ReservationId id = new ReservationId(accountId, productId);
    reservationsJpa.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Product> getReservationProducts(Long accountId, String language) {

    List<ProductTranslationEntity> entities = productTranslationJpa.findTranslationsForReservations(accountId, language);

    return entities.stream()
        .map(productMapper::fromEntity)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReservationMetadata> getUserReservationMetadata(Long accountId) {
    return reservationsJpa.findByIdUserId(accountId).stream()
        .map(entity -> new ReservationMetadata(entity.getId().getProductId(), entity.getAddedAt()))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public int countReservedProducts(Long accountId) {
    return Math.toIntExact(reservationsJpa.countByIdUserId(accountId));
  }

  @Override
  @Transactional(readOnly = true)
  public BigDecimal calculateTotalReservationCost(Long accountId) {
    return reservationsJpa.totalCostOfReservations(accountId);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean exists(Long accountId, UUID productId) {
    return reservationsJpa.existsById(new ReservationId(accountId, productId));
  }
}
