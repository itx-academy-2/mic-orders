package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.order.exception.InsufficientProductQuantityException;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChangeQuantityUseCaseImplTest {

  @InjectMocks
  private ChangeQuantityUseCaseImpl changeQuantityUseCase;

  @Mock
  private ProductRepository productRepository;

  private static final UUID PRODUCT_ID = UUID.randomUUID();

  private static final int VERSION = 1;

  @Test
  void shouldDecreaseProductQuantityWhenDeltaIsPositive() {
    // Given
    Product product = getProductWithQuantity(10, VERSION);
    int delta = 3;
    int expectedNewQuantity = 10 - delta;

    // When
    changeQuantityUseCase.changeQuantityOfProduct(product, delta);

    // Then
    verify(productRepository).setNewProductQuantity(PRODUCT_ID, expectedNewQuantity, VERSION);
  }

  @Test
  void shouldIncreaseProductQuantityWhenDeltaIsNegative() {
    // Given
    Product product = getProductWithQuantity(5, VERSION);
    int delta = -2;
    int expectedNewQuantity = 5 - delta;

    // When
    changeQuantityUseCase.changeQuantityOfProduct(product, delta);

    // Then
    verify(productRepository).setNewProductQuantity(PRODUCT_ID, expectedNewQuantity, VERSION);
  }

  @Test
  void shouldThrowExceptionWhenNewQuantityWouldBeNegative() {
    // Given
    Product product = getProductWithQuantity(2, VERSION);
    int delta = 5;

    // When
    assertThrows(InsufficientProductQuantityException.class,
        () -> changeQuantityUseCase.changeQuantityOfProduct(product, delta));

    // Then
    verify(productRepository, never()).setNewProductQuantity(any(), anyInt(), anyInt());
  }

  @Test
  void shouldPropagateOptimisticLockingFailureException() {
    // Given
    Product product = getProductWithQuantity(10, VERSION);

    doThrow(new OptimisticLockingFailureException("conflict")).when(productRepository).setNewProductQuantity(PRODUCT_ID, 9, VERSION);

    // When / Then
    assertThrows(OptimisticLockingFailureException.class, () -> changeQuantityUseCase.changeQuantityOfProduct(product, 1));
  }

  private Product getProductWithQuantity(int quantity, int version) {
    return Product.builder()
        .id(PRODUCT_ID)
        .quantity(quantity)
        .version(version)
        .build();
  }
}
