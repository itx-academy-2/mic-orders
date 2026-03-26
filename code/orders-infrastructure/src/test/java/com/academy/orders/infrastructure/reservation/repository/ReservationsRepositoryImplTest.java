package com.academy.orders.infrastructure.reservation.repository;

import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.infrastructure.product.ProductMapper;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import com.academy.orders.infrastructure.product.repository.ProductTranslationJpaAdapter;
import com.academy.orders.infrastructure.reservation.entity.ReservationEntity;
import com.academy.orders.infrastructure.reservation.entity.ReservationId;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.academy.orders.infrastructure.TestConstants.TEST_ID;
import static com.academy.orders.infrastructure.TestConstants.TEST_UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationsRepositoryImplTest {

  private static final Long ACCOUNT_ID = TEST_ID;

  private static final UUID PRODUCT_ID = TEST_UUID;

  private static final String LANGUAGE_EN = "en";

  @InjectMocks
  private ReservationsRepositoryImpl repository;

  @Mock
  private UserReservationsJpaAdapter reservationsJpa;

  @Mock
  private ProductTranslationJpaAdapter productTranslationJpa;

  @Mock
  private ProductMapper productMapper;

  @Test
  void lockUserReservationsTest() {
    // Given
    when(reservationsJpa.lockAllByUserId(ACCOUNT_ID)).thenReturn(List.of());

    // When
    repository.lockUserReservations(ACCOUNT_ID);

    // Then
    verify(reservationsJpa, times(1)).lockAllByUserId(ACCOUNT_ID);
  }

  @Test
  void addProductToReservationsShouldCallUpsertTest() {
    // When
    repository.addProductToReservations(ACCOUNT_ID, PRODUCT_ID);

    // Then
    verify(reservationsJpa, times(1)).upsertAndIncrement(ACCOUNT_ID, PRODUCT_ID);
  }

  @Test
  void removeProductFromReservationsTest() {
    // Given
    ReservationId reservationId = new ReservationId(ACCOUNT_ID, PRODUCT_ID);

    // When
    repository.removeProductFromReservations(ACCOUNT_ID, PRODUCT_ID);

    // Then
    verify(reservationsJpa, times(1)).deleteById(reservationId);
  }

  @Test
  void decrementProductQuantityShouldDecreaseQuantityWhenGreaterThanOneTest() {
    // Given
    ReservationId reservationId = new ReservationId(ACCOUNT_ID, PRODUCT_ID);
    ReservationEntity entity = ReservationEntity.builder()
        .id(reservationId)
        .addedAt(Instant.now())
        .quantity(3)
        .build();
    when(reservationsJpa.findById(reservationId)).thenReturn(Optional.of(entity));

    // When
    repository.decrementProductReservationQuantity(ACCOUNT_ID, PRODUCT_ID);

    // Then
    assertEquals(2, entity.getQuantity());
    verify(reservationsJpa, times(1)).findById(reservationId);
    verify(reservationsJpa, never()).deleteById(any());
  }

  @Test
  void decrementProductQuantityShouldDeleteWhenQuantityIsOneTest() {
    // Given
    ReservationId reservationId = new ReservationId(ACCOUNT_ID, PRODUCT_ID);
    ReservationEntity entity = ReservationEntity.builder()
        .id(reservationId)
        .addedAt(Instant.now())
        .quantity(1)
        .build();
    when(reservationsJpa.findById(reservationId)).thenReturn(Optional.of(entity));

    // When
    repository.decrementProductReservationQuantity(ACCOUNT_ID, PRODUCT_ID);

    // Then
    verify(reservationsJpa, times(1)).deleteById(reservationId);
    verify(reservationsJpa, times(1)).findById(reservationId);
  }

  @Test
  void decrementProductQuantityShouldDoNothingWhenReservationDoesNotExistTest() {
    // Given
    ReservationId reservationId = new ReservationId(ACCOUNT_ID, PRODUCT_ID);
    when(reservationsJpa.findById(reservationId)).thenReturn(Optional.empty());

    // When
    repository.decrementProductReservationQuantity(ACCOUNT_ID, PRODUCT_ID);

    // Then
    verify(reservationsJpa, times(1)).findById(reservationId);
    verify(reservationsJpa, never()).deleteById(any());
  }

  @Test
  void getReservationProductsTest() {
    // Given
    var translationEntity = mock(ProductTranslationEntity.class);
    var domainProduct = mock(Product.class);

    when(productTranslationJpa.findTranslationsForReservations(ACCOUNT_ID, LANGUAGE_EN)).thenReturn(List.of(translationEntity));
    when(productMapper.fromEntity(translationEntity)).thenReturn(domainProduct);

    // When
    var result = repository.getReservationProducts(ACCOUNT_ID, LANGUAGE_EN);

    // Then
    assertEquals(1, result.size());
    assertEquals(domainProduct, result.get(0));
    verify(productTranslationJpa, times(1))
        .findTranslationsForReservations(ACCOUNT_ID, LANGUAGE_EN);
    verify(productMapper, times(1)).fromEntity(translationEntity);
  }

  @Test
  void getUserReservationMetadataTest() {
    // Given
    UUID productId = UUID.randomUUID();
    Instant addedAt = Instant.parse("2025-03-03T10:15:30Z");

    ReservationId reservationId = new ReservationId(ACCOUNT_ID, productId);
    ReservationEntity entity = mock(ReservationEntity.class);

    when(entity.getId()).thenReturn(reservationId);
    when(entity.getAddedAt()).thenReturn(addedAt);
    when(entity.getQuantity()).thenReturn(3);

    when(reservationsJpa.findByIdUserId(ACCOUNT_ID)).thenReturn(List.of(entity));

    // When
    var result = repository.getUserReservationMetadata(ACCOUNT_ID);

    // Then
    assertEquals(1, result.size());
    assertEquals(productId, result.get(0).productId());
    assertEquals(addedAt, result.get(0).reservedAt());
    assertEquals(3, result.get(0).reservedQuantity());
    verify(reservationsJpa, times(1)).findByIdUserId(ACCOUNT_ID);
  }

  @Test
  void getUserReservationsShouldReturnEmptyListWhenNoReservationMetadataTest() {
    // Given
    when(reservationsJpa.findByIdUserId(ACCOUNT_ID)).thenReturn(List.of());

    // When
    var result = repository.getUserReservationMetadata(ACCOUNT_ID);

    // Then
    assertTrue(result.isEmpty());
    verify(reservationsJpa, times(1)).findByIdUserId(ACCOUNT_ID);
  }

  @Test
  void sumReservedQuantityTest() {
    // Given
    when(reservationsJpa.sumReservedQuantity(ACCOUNT_ID)).thenReturn(3);

    // When
    int result = repository.sumReservedQuantity(ACCOUNT_ID);

    // Then
    assertEquals(3, result);
    verify(reservationsJpa, times(1)).sumReservedQuantity(ACCOUNT_ID);
  }

  @Test
  void calculateTotalReservationCostTest() {
    // Given
    BigDecimal total = new BigDecimal("1200.50");
    when(reservationsJpa.totalCostOfReservations(ACCOUNT_ID)).thenReturn(total);

    // When
    BigDecimal result = repository.calculateTotalReservationCost(ACCOUNT_ID);

    // Then
    assertEquals(total, result);
    verify(reservationsJpa, times(1)).totalCostOfReservations(ACCOUNT_ID);
  }

  @Test
  void existsShouldReturnTrueWhenReservationExistsTest() {
    // Given
    ReservationId reservationId = new ReservationId(ACCOUNT_ID, PRODUCT_ID);
    when(reservationsJpa.existsById(reservationId)).thenReturn(true);

    // When
    boolean result = repository.exists(ACCOUNT_ID, PRODUCT_ID);

    // Then
    assertTrue(result);
    verify(reservationsJpa, times(1)).existsById(reservationId);
  }

  @Test
  void existsShouldReturnFalseWhenReservationDoesNotExistTest() {
    // Given
    ReservationId reservationId = new ReservationId(ACCOUNT_ID, PRODUCT_ID);
    when(reservationsJpa.existsById(reservationId)).thenReturn(false);

    // When
    boolean result = repository.exists(ACCOUNT_ID, PRODUCT_ID);

    // Then
    assertFalse(result);
    verify(reservationsJpa, times(1)).existsById(reservationId);
  }

  @Test
  void getReservedQuantitiesByProductIdsTest() {
    // Given
    UUID productId = UUID.randomUUID();
    List<UUID> productIds = List.of(productId);

    Tuple tuple = mock(Tuple.class);
    when(tuple.get("productId", UUID.class)).thenReturn(productId);
    when(tuple.get("reservedQuantity", Long.class)).thenReturn(2L);
    when(reservationsJpa.sumReservedProductsByProductIds(productIds)).thenReturn(List.of(tuple));

    // When
    var result = repository.getReservedQuantitiesByProductIds(productIds);

    // Then
    assertEquals(1, result.size());
    assertEquals(2L, result.get(productId));
    verify(reservationsJpa, times(1)).sumReservedProductsByProductIds(productIds);
  }

  @Test
  void getReservedQuantityShouldReturnQuantityWhenReservationExistsTest() {
    // Given
    int quantity = 3;
    ReservationId reservationId = new ReservationId(ACCOUNT_ID, PRODUCT_ID);
    ReservationEntity entity = ReservationEntity.builder()
        .id(reservationId)
        .addedAt(Instant.now())
        .quantity(quantity)
        .build();

    when(reservationsJpa.findById(reservationId)).thenReturn(Optional.of(entity));

    // When
    int result = repository.getReservedQuantity(ACCOUNT_ID, PRODUCT_ID);

    // Then
    assertEquals(quantity, result);
    verify(reservationsJpa, times(1)).findById(reservationId);
  }

  @Test
  void getReservedQuantityShouldReturnZeroWhenReservationDoesNotExistTest() {
    // Given
    ReservationId reservationId = new ReservationId(ACCOUNT_ID, PRODUCT_ID);

    when(reservationsJpa.findById(reservationId)).thenReturn(Optional.empty());

    // When
    int result = repository.getReservedQuantity(ACCOUNT_ID, PRODUCT_ID);

    // Then
    assertEquals(0, result);
    verify(reservationsJpa, times(1)).findById(reservationId);
  }

  @Test
  void getReservedQuantitiesByProductIdsShouldReturnEmptyMapWhenInputEmptyTest() {
    // When
    var result = repository.getReservedQuantitiesByProductIds(List.of());

    // Then
    assertTrue(result.isEmpty());
    verify(reservationsJpa, never()).sumReservedProductsByProductIds(any());
  }

  @Test
  void getReservedQuantitiesByProductIdsShouldReturnEmptyMapWhenInputNullTest() {
    // When
    var result = repository.getReservedQuantitiesByProductIds(null);

    // Then
    assertTrue(result.isEmpty());
    verify(reservationsJpa, never()).sumReservedProductsByProductIds(any());
  }

  @Test
  void getReservedQuantitiesByProductIdsShouldReturnEmptyMapWhenDbReturnsNothingTest() {
    // Given
    UUID productId = UUID.randomUUID();
    List<UUID> productIds = List.of(productId);

    when(reservationsJpa.sumReservedProductsByProductIds(productIds))
        .thenReturn(List.of());

    // When
    var result = repository.getReservedQuantitiesByProductIds(productIds);

    // Then
    assertTrue(result.isEmpty());
    verify(reservationsJpa, times(1)).sumReservedProductsByProductIds(productIds);
  }
}
