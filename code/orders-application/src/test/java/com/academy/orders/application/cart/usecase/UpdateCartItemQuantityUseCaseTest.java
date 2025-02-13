package com.academy.orders.application.cart.usecase;

import com.academy.orders.domain.cart.dto.UpdatedCartItemDto;
import com.academy.orders.domain.cart.entity.CartItem;
import com.academy.orders.domain.cart.entity.CreateCartItemDTO;
import com.academy.orders.domain.cart.exception.CartItemNotFoundException;
import com.academy.orders.domain.cart.exception.QuantityExceedsAvailableException;
import com.academy.orders.domain.cart.repository.CartItemRepository;
import com.academy.orders.domain.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.academy.orders.application.ModelUtils.getCartItem;
import static com.academy.orders.application.ModelUtils.getCartItemWithDiscount;
import static com.academy.orders.application.ModelUtils.getProductWithQuantity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCartItemQuantityUseCaseTest {
  @InjectMocks
  private UpdateCartItemQuantityUseCaseImpl updateCartItemQuantityUseCase;

  @Mock
  private CartItemRepository cartItemRepository;

  @Test
  void setQuantitySuccessfulUpdateTest() {
    UUID productId = UUID.randomUUID();
    Long userId = 1L;
    Integer quantity = 2;

    Product product = getProductWithQuantity(5);

    CartItem cartItem = getCartItem(product, 1);
    List<CartItem> cartItems = List.of(cartItem);

    when(cartItemRepository.existsByProductIdAndUserId(productId, userId)).thenReturn(true);
    when(cartItemRepository.findByProductIdAndUserId(productId, userId)).thenReturn(Optional.of(cartItem));
    when(cartItemRepository.save(any(CreateCartItemDTO.class))).thenReturn(new CartItem(product, quantity));
    when(cartItemRepository.findCartItemsByAccountId(userId)).thenReturn(cartItems);

    UpdatedCartItemDto updatedCartItemDto = updateCartItemQuantityUseCase.setQuantity(productId, userId, quantity);

    assertNotNull(updatedCartItemDto);
    assertEquals(productId, updatedCartItemDto.productId());
    assertEquals(quantity, updatedCartItemDto.quantity());
    assertEquals(product.getPrice(), updatedCartItemDto.productPrice());
    assertEquals(BigDecimal.valueOf(1999.98), updatedCartItemDto.calculatedPrice());
    assertEquals(BigDecimal.valueOf(999.99), updatedCartItemDto.totalPrice());
    assertNull(updatedCartItemDto.calculatedPriceWithDiscount());
    assertNull(updatedCartItemDto.totalPriceWithDiscount());
    assertNull(updatedCartItemDto.productPriceWithDiscount());
    assertNull(updatedCartItemDto.discount());

    verify(cartItemRepository).existsByProductIdAndUserId(productId, userId);
    verify(cartItemRepository).findByProductIdAndUserId(productId, userId);
    verify(cartItemRepository).save(any(CreateCartItemDTO.class));
    verify(cartItemRepository).findCartItemsByAccountId(userId);
  }

  @Test
  void setQuantityWhenProductHasDiscount() {
    final UUID productId = UUID.randomUUID();
    final Long userId = 1L;
    final BigDecimal quantity = BigDecimal.valueOf(9);
    final CartItem cartItemBefore = getCartItemWithDiscount();
    final CartItem cartItemAfter = new CartItem(cartItemBefore.product(), quantity.intValue());
    final Product product = cartItemAfter.product();
    final BigDecimal sumOfAllPrices = product.getPrice().multiply(quantity);
    final BigDecimal sumOfAllDiscountedPrices = product.getPriceWithDiscount().multiply(quantity);

    when(cartItemRepository.existsByProductIdAndUserId(productId, userId)).thenReturn(true);
    when(cartItemRepository.findByProductIdAndUserId(productId, userId)).thenReturn(Optional.of(cartItemBefore));
    when(cartItemRepository.save(any(CreateCartItemDTO.class))).thenReturn(cartItemAfter);
    when(cartItemRepository.findCartItemsByAccountId(userId)).thenReturn(List.of(cartItemAfter));

    final UpdatedCartItemDto result = updateCartItemQuantityUseCase.setQuantity(productId, userId, quantity.intValue());

    assertNotNull(result);
    assertEquals(productId, result.productId());
    assertEquals(quantity.intValue(), result.quantity());
    assertEquals(product.getPrice(), result.productPrice());
    assertEquals(product.getPriceWithDiscount(), result.productPriceWithDiscount());
    assertEquals(product.getDiscountAmount(), result.discount());
    assertEquals(sumOfAllPrices, result.totalPrice());
    assertEquals(sumOfAllDiscountedPrices, result.totalPriceWithDiscount());
    assertEquals(sumOfAllPrices, result.calculatedPrice());
    assertEquals(sumOfAllDiscountedPrices, result.calculatedPriceWithDiscount());

    verify(cartItemRepository).existsByProductIdAndUserId(productId, userId);
    verify(cartItemRepository).findByProductIdAndUserId(productId, userId);
    verify(cartItemRepository).save(any(CreateCartItemDTO.class));
    verify(cartItemRepository).findCartItemsByAccountId(userId);
  }

  @Test
  void setQuantityCartItemNotFoundTest() {
    UUID productId = UUID.randomUUID();
    Long userId = 1L;
    int quantity = 2;

    when(cartItemRepository.existsByProductIdAndUserId(productId, userId)).thenReturn(false);

    assertThrows(CartItemNotFoundException.class,
        () -> updateCartItemQuantityUseCase.setQuantity(productId, userId, quantity));

    verify(cartItemRepository).existsByProductIdAndUserId(productId, userId);
    verify(cartItemRepository, never()).findByProductIdAndUserId(any(UUID.class), anyLong());
    verify(cartItemRepository, never()).save(any(CreateCartItemDTO.class));
  }

  @Test
  void setQuantityExceedsAvailableExceptionTest() {
    UUID productId = UUID.randomUUID();
    Long userId = 1L;
    Integer quantity = 6;
    Integer availableQuantity = 5;

    Product product = getProductWithQuantity(5);

    CartItem cartItem = getCartItem(product, 1);

    when(cartItemRepository.existsByProductIdAndUserId(productId, userId)).thenReturn(true);
    when(cartItemRepository.findByProductIdAndUserId(productId, userId)).thenReturn(Optional.of(cartItem));
    when(cartItemRepository.save(any(CreateCartItemDTO.class))).thenReturn(new CartItem(product, quantity));

    QuantityExceedsAvailableException exception = assertThrows(QuantityExceedsAvailableException.class,
        () -> updateCartItemQuantityUseCase.setQuantity(productId, userId, quantity));

    assertEquals(
        String.format("Product with id: %s exceeded available quantity. Requested: %d, Available: %d",
            productId, quantity, availableQuantity),
        exception.getMessage());

    verify(cartItemRepository).existsByProductIdAndUserId(productId, userId);
    verify(cartItemRepository).findByProductIdAndUserId(productId, userId);
    verify(cartItemRepository).save(any(CreateCartItemDTO.class));
  }

  @Test
  void setQuantityEqualProductQuantityTest() {
    UUID productId = UUID.randomUUID();
    Long userId = 1L;
    Integer quantity = 5;
    Product product = getProductWithQuantity(5);
    CartItem cartItem = getCartItem(product, 1);
    List<CartItem> cartItems = List.of(cartItem);

    when(cartItemRepository.existsByProductIdAndUserId(productId, userId)).thenReturn(true);
    when(cartItemRepository.findByProductIdAndUserId(productId, userId)).thenReturn(Optional.of(cartItem));
    when(cartItemRepository.save(any(CreateCartItemDTO.class))).thenReturn(new CartItem(product, quantity));
    when(cartItemRepository.findCartItemsByAccountId(userId)).thenReturn(cartItems);

    UpdatedCartItemDto updatedCartItemDto = updateCartItemQuantityUseCase.setQuantity(productId, userId, quantity);

    assertEquals(productId, updatedCartItemDto.productId());
    assertEquals(quantity, updatedCartItemDto.quantity());
    assertEquals(product.getPrice(), updatedCartItemDto.productPrice());
    assertEquals(BigDecimal.valueOf(4999.95), updatedCartItemDto.calculatedPrice());
    assertEquals(BigDecimal.valueOf(999.99), updatedCartItemDto.totalPrice());

    verify(cartItemRepository).existsByProductIdAndUserId(productId, userId);
    verify(cartItemRepository).findByProductIdAndUserId(productId, userId);
    verify(cartItemRepository).save(any(CreateCartItemDTO.class));
    verify(cartItemRepository).findCartItemsByAccountId(userId);
  }

  @Test
  void setQuantityUpdateQuantityOfCartItemReturnsNullTest() {
    UUID productId = UUID.randomUUID();
    Long userId = 1L;
    Integer quantity = 2;

    when(cartItemRepository.existsByProductIdAndUserId(productId, userId)).thenReturn(true);
    when(cartItemRepository.findByProductIdAndUserId(productId, userId)).thenReturn(Optional.empty());

    assertThrows(CartItemNotFoundException.class,
        () -> updateCartItemQuantityUseCase.setQuantity(productId, userId, quantity));

    verify(cartItemRepository).existsByProductIdAndUserId(productId, userId);
    verify(cartItemRepository).findByProductIdAndUserId(productId, userId);
    verify(cartItemRepository, never()).save(any(CreateCartItemDTO.class));
  }
}
