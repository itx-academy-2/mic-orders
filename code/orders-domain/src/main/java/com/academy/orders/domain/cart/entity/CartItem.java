package com.academy.orders.domain.cart.entity;

import com.academy.orders.domain.product.entity.Product;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Builder
public record CartItem(Product product, Integer quantity) {
  public static BigDecimal calculateCartItemPrice(CartItem cartItem) {
    return cartItem.product().getPrice().multiply(new BigDecimal(cartItem.quantity()));
  }

  public static BigDecimal calculateCartTotalPrice(List<CartItem> cartItems) {
    return cartItems.stream().map(CartItem::calculateCartItemPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public static BigDecimal calculateCartItemPriceWithDiscount(CartItem cartItem) {
    final BigDecimal priceWithDiscount = cartItem.product().getPriceWithDiscount();
    return priceWithDiscount == null ? null : priceWithDiscount.multiply(new BigDecimal(cartItem.quantity()));
  }

  public static BigDecimal calculateTotalPriceWithDiscount(final List<CartItem> cartItems) {
    boolean anyProductWithDiscount = false;
    BigDecimal totalPriceWithDiscount = BigDecimal.ZERO;

    for (CartItem cartItem : cartItems) {
      final BigDecimal priceWithDiscount = cartItem.product().getPriceWithDiscount();
      if (Objects.nonNull(priceWithDiscount)) {
        anyProductWithDiscount = true;
        totalPriceWithDiscount = totalPriceWithDiscount.add(calculateCartItemPriceWithDiscount(cartItem));
      } else {
        totalPriceWithDiscount = totalPriceWithDiscount.add(calculateCartItemPrice(cartItem));
      }
    }

    return anyProductWithDiscount ? totalPriceWithDiscount : null;
  }
}
