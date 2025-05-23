package com.academy.orders.application.cart.usecase;

import com.academy.orders.domain.account.entity.Account;
import com.academy.orders.domain.cart.entity.CartItem;
import com.academy.orders.domain.cart.entity.CreateCartItemDTO;
import com.academy.orders.domain.cart.exception.CartItemAlreadyExists;
import com.academy.orders.domain.cart.repository.CartItemRepository;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.exception.ProductNotFoundException;
import com.academy.orders.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.academy.orders.application.ModelUtils.getAccount;
import static com.academy.orders.application.ModelUtils.getProductWithImageLink;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCartItemByUserUseCaseTest {
  @InjectMocks
  private CreateCartItemByUserUseCaseImpl createCartItemByUserUseCase;

  @Mock
  private CartItemRepository cartItemRepository;

  @Mock
  private ProductRepository productRepository;

  private CreateCartItemDTO cartItemDTO;

  private Product product;

  @BeforeEach
  void setUp() {
    Account account = getAccount();
    product = getProductWithImageLink();
    cartItemDTO = CreateCartItemDTO.builder().productId(product.getId()).userId(account.id()).quantity(1).build();
  }

  @Test
  void createCartItemByUserIfProductIsNotAddedToTheCartTest() {
    var cartItem = CartItem.builder().product(product).quantity(1).build();

    when(productRepository.existById(product.getId())).thenReturn(true);
    when(cartItemRepository.existsByProductIdAndUserId(cartItemDTO.productId(), cartItemDTO.userId()))
        .thenReturn(false);
    when(cartItemRepository.save(cartItemDTO)).thenReturn(cartItem);

    assertDoesNotThrow(() -> createCartItemByUserUseCase.create(cartItemDTO));

    verify(productRepository).existById(any(UUID.class));
    verify(cartItemRepository).existsByProductIdAndUserId(any(UUID.class), anyLong());
    verify(cartItemRepository).save(cartItemDTO);
  }

  @Test
  void createCartItemByUserIfProductIsAddedToTheCartTest() {
    when(productRepository.existById(product.getId())).thenReturn(true);
    when(cartItemRepository.existsByProductIdAndUserId(cartItemDTO.productId(), cartItemDTO.userId()))
        .thenReturn(true);

    assertThrows(CartItemAlreadyExists.class, () -> createCartItemByUserUseCase.create(cartItemDTO));

    verify(productRepository).existById(any(UUID.class));
    verify(cartItemRepository).existsByProductIdAndUserId(any(UUID.class), anyLong());
    verify(cartItemRepository, never()).save(cartItemDTO);

  }

  @Test
  void createCartItemByUserIfProductDoesNotExistsTest() {
    when(productRepository.existById(product.getId())).thenReturn(false);

    assertThrowsExactly(ProductNotFoundException.class, () -> createCartItemByUserUseCase.create(cartItemDTO));
    verify(productRepository).existById(any(UUID.class));
  }
}
