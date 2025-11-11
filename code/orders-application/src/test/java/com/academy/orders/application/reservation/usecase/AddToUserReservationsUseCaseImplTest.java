package com.academy.orders.application.reservation.usecase;

import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import com.academy.orders.domain.product.exception.ProductNotFoundException;
import com.academy.orders.domain.product.exception.ProductNotVisibleException;
import com.academy.orders.domain.product.exception.ProductOutOfStockException;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.ChangeQuantityUseCase;
import com.academy.orders.domain.reservation.exception.ReservationLimitExceededException;
import com.academy.orders.domain.reservation.exception.ReservationTotalCostExceededException;
import com.academy.orders.domain.reservation.repository.ReservationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static com.academy.orders.application.TestConstants.TEST_ID;
import static com.academy.orders.application.TestConstants.TEST_UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddToUserReservationsUseCaseImplTest {

  private static final Long TEST_USER_ID = TEST_ID;

  private static final UUID TEST_PRODUCT_ID = TEST_UUID;

  @InjectMocks
  private AddToUserReservationsUseCaseImpl addToUserReservationsUseCase;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ReservationsRepository reservationsRepository;

  @Mock
  private ChangeQuantityUseCase changeQuantityUseCase;

  @Test
  void addProductToReservationsWhenProductNotFoundTest() {
    // Given
    when(productRepository.getById(TEST_PRODUCT_ID)).thenReturn(Optional.empty());

    // When
    assertThrows(ProductNotFoundException.class,
        () -> addToUserReservationsUseCase.addProductToReservations(TEST_USER_ID, TEST_PRODUCT_ID));

    // Then
    verify(productRepository, times(1)).getById(TEST_PRODUCT_ID);
    verifyNoInteractions(reservationsRepository, changeQuantityUseCase);
  }

  @Test
  void addProductToReservationsWhenProductNotVisibleTest() {
    // Given
    Product product = Product.builder()
        .id(TEST_PRODUCT_ID)
        .status(ProductStatus.HIDDEN)
        .price(BigDecimal.TEN)
        .quantity(10)
        .build();
    when(productRepository.getById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));

    // When
    assertThrows(ProductNotVisibleException.class,
        () -> addToUserReservationsUseCase.addProductToReservations(TEST_USER_ID, TEST_PRODUCT_ID));

    // Then
    verify(productRepository, times(1)).getById(TEST_PRODUCT_ID);
    verifyNoMoreInteractions(productRepository);
    verifyNoInteractions(reservationsRepository, changeQuantityUseCase);
  }

  @Test
  void addProductToReservationsWhenProductAlreadyReservedTest() {
    // Given
    Product product = Product.builder()
        .id(TEST_PRODUCT_ID)
        .status(ProductStatus.VISIBLE)
        .price(BigDecimal.TEN)
        .quantity(10)
        .build();
    when(productRepository.getById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));
    when(reservationsRepository.exists(TEST_USER_ID, TEST_PRODUCT_ID)).thenReturn(true);

    // When
    addToUserReservationsUseCase.addProductToReservations(TEST_USER_ID, TEST_PRODUCT_ID);

    // Then
    verify(reservationsRepository, times(1)).exists(TEST_USER_ID, TEST_PRODUCT_ID);
    verify(reservationsRepository, never()).addProductToReservations(any(), any());
    verifyNoInteractions(changeQuantityUseCase);
  }

  @Test
  void addProductToReservationsWhenProductOutOfStockTest() {
    // Given
    Product product = Product.builder()
        .id(TEST_PRODUCT_ID)
        .status(ProductStatus.VISIBLE)
        .quantity(0)
        .price(BigDecimal.TEN)
        .build();
    when(productRepository.getById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));
    when(reservationsRepository.exists(TEST_USER_ID, TEST_PRODUCT_ID)).thenReturn(false);

    // When
    assertThrows(ProductOutOfStockException.class,
        () -> addToUserReservationsUseCase.addProductToReservations(TEST_USER_ID, TEST_PRODUCT_ID));

    // Then
    verify(reservationsRepository, never()).addProductToReservations(any(), any());
    verifyNoInteractions(changeQuantityUseCase);
  }

  @Test
  void addProductToReservationsWhenLimitExceededTest() {
    // Given
    Product product = Product.builder()
        .id(TEST_PRODUCT_ID)
        .status(ProductStatus.VISIBLE)
        .quantity(10)
        .price(BigDecimal.TEN)
        .build();
    when(productRepository.getById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));
    when(reservationsRepository.exists(TEST_USER_ID, TEST_PRODUCT_ID)).thenReturn(false);
    when(reservationsRepository.countReservedProducts(TEST_USER_ID)).thenReturn(5);

    // When
    assertThrows(ReservationLimitExceededException.class,
        () -> addToUserReservationsUseCase.addProductToReservations(TEST_USER_ID, TEST_PRODUCT_ID));

    // Then
    verify(reservationsRepository, never()).addProductToReservations(any(), any());
  }

  @Test
  void addProductToReservationsWhenTotalCostExceededTest() {
    // Given
    Product product = Product.builder()
        .id(TEST_PRODUCT_ID)
        .status(ProductStatus.VISIBLE)
        .quantity(10)
        .price(new BigDecimal("1000"))
        .build();
    when(productRepository.getById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));
    when(reservationsRepository.exists(TEST_USER_ID, TEST_PRODUCT_ID)).thenReturn(false);
    when(reservationsRepository.countReservedProducts(TEST_USER_ID)).thenReturn(2);
    when(reservationsRepository.calculateTotalReservationCost(TEST_USER_ID))
        .thenReturn(new BigDecimal("4500"));

    // When
    assertThrows(ReservationTotalCostExceededException.class,
        () -> addToUserReservationsUseCase.addProductToReservations(TEST_USER_ID, TEST_PRODUCT_ID));

    // Then
    verify(reservationsRepository, never()).addProductToReservations(any(), any());
  }

  @Test
  void addProductToReservationsSuccessTest() {
    // Given
    Product product = Product.builder()
        .id(TEST_PRODUCT_ID)
        .status(ProductStatus.VISIBLE)
        .quantity(10)
        .price(new BigDecimal("100"))
        .build();
    when(productRepository.getById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));
    when(reservationsRepository.exists(TEST_USER_ID, TEST_PRODUCT_ID)).thenReturn(false);
    when(reservationsRepository.countReservedProducts(TEST_USER_ID)).thenReturn(2);
    when(reservationsRepository.calculateTotalReservationCost(TEST_USER_ID)).thenReturn(BigDecimal.ZERO);

    // When
    addToUserReservationsUseCase.addProductToReservations(TEST_USER_ID, TEST_PRODUCT_ID);

    // Then
    verify(changeQuantityUseCase, times(1)).changeQuantityOfProduct(product, 1);
    verify(reservationsRepository, times(1)).addProductToReservations(TEST_USER_ID, TEST_PRODUCT_ID);
  }
}
