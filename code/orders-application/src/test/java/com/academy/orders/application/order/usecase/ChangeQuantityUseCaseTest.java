package com.academy.orders.application.order.usecase;

import com.academy.orders.application.product.usecase.ChangeQuantityUseCaseImpl;
import com.academy.orders.domain.order.exception.InsufficientProductQuantityException;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChangeQuantityUseCaseImplTest {

  @InjectMocks
  private ChangeQuantityUseCaseImpl changeQuantityUseCase;

  @Mock
  private ProductRepository productRepository;

  private static final UUID PRODUCT_ID = UUID.randomUUID();

  @Test
  void shouldDecreaseProductQuantityWhenDeltaIsPositive() {
    // Given
    Product product = getProductWithQuantity(10);
    int delta = 3;
    int expectedNewQuantity = 10 - delta;

    // When
    changeQuantityUseCase.changeQuantityOfProduct(product, delta);

    // Then
    verify(productRepository).setNewProductQuantity(PRODUCT_ID, expectedNewQuantity);
  }

  @Test
  void shouldIncreaseProductQuantityWhenDeltaIsNegative() {
    // Given
    Product product = getProductWithQuantity(5);
    int delta = -2;
    int expectedNewQuantity = 5 - delta;

    // When
    changeQuantityUseCase.changeQuantityOfProduct(product, delta);

    // Then
    verify(productRepository).setNewProductQuantity(PRODUCT_ID, expectedNewQuantity);
  }

  @Test
  void shouldThrowExceptionWhenNewQuantityWouldBeNegative() {
    // Given
    Product product = getProductWithQuantity(2);
    int delta = 5;

    // When
    assertThrows(InsufficientProductQuantityException.class,
        () -> changeQuantityUseCase.changeQuantityOfProduct(product, delta));

    // Then
    verify(productRepository, never())
        .setNewProductQuantity(any(UUID.class), anyInt());
  }

  private Product getProductWithQuantity(int quantity) {
    return Product.builder()
        .id(PRODUCT_ID)
        .quantity(quantity)
        .build();
  }
}
