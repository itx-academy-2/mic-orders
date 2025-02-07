package com.academy.orders.domain.cart.usecase;

import com.academy.orders.domain.cart.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Use case interface for calculating price for cart item.
 */
public interface CalculatePriceUseCase {

  /**
   * Method calculates price for cartItem.
   *
   * @return {@link BigDecimal} calculated price
   * @author Denys Ryhal
   */
  BigDecimal calculateCartItemPrice(CartItem cartItem);

  /**
   * Method calculates total price of cart.
   *
   * @return {@link BigDecimal} total price of cart
   * @author Denys Ryhal
   */
  BigDecimal calculateCartTotalPrice(List<CartItem> cartItems);

  /**
   * Calculates the price with a discount for a given CartItem. If no discount is applied, returns null.
   *
   * @param cartItem The CartItem for which the price with discount is calculated.
   * @return {@link BigDecimal} The calculated price with discount, or null if no discount is applied.
   * @author Vitalii Fedyk
   */
  BigDecimal calculateCartItemPriceWithDiscount(CartItem cartItem);

  /**
   * Calculates the total price of the discounted products in the cart. If there is at least one discounted product, the total price is
   * returned; otherwise, null is returned.
   *
   * @return {@link BigDecimal} the total price of the discounted products in the cart, or null if no discounted products are present.
   * @author Vitalii Fedyk
   */
  BigDecimal calculateTotalPriceWithDiscount(List<CartItem> cartItems);
}
