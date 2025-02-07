package com.academy.orders.application.cart.usecase;

import com.academy.orders.domain.cart.entity.CartItem;
import com.academy.orders.domain.cart.usecase.CalculatePriceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CalculatePriceUseCaseImpl implements CalculatePriceUseCase {

  @Override
  public BigDecimal calculateCartItemPrice(CartItem cartItem) {
    return cartItem.product().getPrice().multiply(new BigDecimal(cartItem.quantity()));
  }

  @Override
  public BigDecimal calculateCartTotalPrice(List<CartItem> cartItems) {
    return cartItems.stream().map(this::calculateCartItemPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Override
  public BigDecimal calculateCartItemPriceWithDiscount(CartItem cartItem) {
    final BigDecimal priceWithDiscount = cartItem.product().getPriceWithDiscount();
    return priceWithDiscount == null ? null : priceWithDiscount.multiply(new BigDecimal(cartItem.quantity()));
  }

  @Override
  public BigDecimal calculateTotalPriceWithDiscount(final List<CartItem> cartItems) {
    boolean anyProductWithDiscount = false;
    BigDecimal totalPriceWithDiscount = BigDecimal.ZERO;

    for (CartItem cartItem : cartItems) {
      final BigDecimal priceWithDiscount = cartItem.product().getPriceWithDiscount();
      if (Objects.nonNull(priceWithDiscount)) {
        anyProductWithDiscount = true;
        totalPriceWithDiscount = totalPriceWithDiscount.add(priceWithDiscount.multiply(new BigDecimal(cartItem.quantity())));
      } else {
        totalPriceWithDiscount = totalPriceWithDiscount.add(calculateCartItemPrice(cartItem));
      }
    }

    return anyProductWithDiscount ? totalPriceWithDiscount : null;
  }
}
