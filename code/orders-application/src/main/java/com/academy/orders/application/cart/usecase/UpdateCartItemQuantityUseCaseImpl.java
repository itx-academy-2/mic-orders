package com.academy.orders.application.cart.usecase;

import com.academy.orders.domain.cart.dto.UpdatedCartItemDto;
import com.academy.orders.domain.cart.entity.CartItem;
import com.academy.orders.domain.cart.entity.CreateCartItemDTO;
import com.academy.orders.domain.cart.exception.CartItemNotFoundException;
import com.academy.orders.domain.cart.exception.QuantityExceedsAvailableException;
import com.academy.orders.domain.cart.repository.CartItemRepository;
import com.academy.orders.domain.cart.usecase.UpdateCartItemQuantityUseCase;
import com.academy.orders.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCartItemQuantityUseCaseImpl implements UpdateCartItemQuantityUseCase {
  private final CartItemRepository cartItemRepository;

  @Override
  @Transactional
  public UpdatedCartItemDto setQuantity(UUID productId, Long userId, Integer quantity) {

    checkCartItemExists(productId, userId);

    var updatedCartItem = updateQuantityOfCartItem(quantity, productId, userId);
    var getAll = cartItemRepository.findCartItemsByAccountId(userId);

    Product product = updatedCartItem.product();

    if (quantity > product.getQuantity()) {
      throw new QuantityExceedsAvailableException(productId, quantity, product.getQuantity());
    }

    var cartItemPrice = CartItem.calculateCartItemPrice(updatedCartItem);
    var totalPrice = CartItem.calculateCartTotalPrice(getAll);

    final BigDecimal cartItemPriceWithDiscount = CartItem.calculateCartItemPriceWithDiscount(updatedCartItem);
    final BigDecimal totalPriceWithDiscount = CartItem.calculateTotalPriceWithDiscount(getAll);

    return new UpdatedCartItemDto(productId, quantity, product.getPrice(), product.getPriceWithDiscount(),
        product.getDiscountAmount(), cartItemPrice, cartItemPriceWithDiscount,
        totalPrice, totalPriceWithDiscount);
  }

  private void checkCartItemExists(UUID productId, Long userId) {
    if (!Boolean.TRUE.equals(cartItemRepository.existsByProductIdAndUserId(productId, userId))) {
      throw new CartItemNotFoundException(productId);
    }
  }

  private CartItem updateQuantityOfCartItem(Integer quantity, UUID productId, Long userId) {
    CartItem cartItem = cartItemRepository.findByProductIdAndUserId(productId, userId)
        .orElseThrow(() -> new CartItemNotFoundException(productId));

    return cartItemRepository.save(new CreateCartItemDTO(productId, userId, quantity));
  }
}
