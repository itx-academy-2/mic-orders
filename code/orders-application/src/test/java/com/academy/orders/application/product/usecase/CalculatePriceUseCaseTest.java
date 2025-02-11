package com.academy.orders.application.product.usecase;

import com.academy.orders.application.cart.usecase.CalculatePriceUseCaseImpl;
import com.academy.orders.domain.cart.entity.CartItem;
import com.academy.orders.domain.product.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.academy.orders.application.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CalculatePriceUseCaseTest {
  private final CalculatePriceUseCaseImpl calculatePriceUseCase = new CalculatePriceUseCaseImpl();

  private static List<CartItem> cartItems() {
    return List.of(
        CartItem.builder().product(Product.builder().price(BigDecimal.valueOf(1500)).build()).quantity(10)
            .build(),
        CartItem.builder().product(Product.builder().price(BigDecimal.valueOf(2000)).build()).quantity(5)
            .build(),
        CartItem.builder().product(Product.builder().price(BigDecimal.valueOf(100)).build()).quantity(2)
            .build());
  }

  @Test
  void calculatePriceTest() {
    var cartItem = getCartItem();
    var expectedResult = cartItem.product().getPrice().multiply(BigDecimal.valueOf(cartItem.quantity()));

    var actualPrice = calculatePriceUseCase.calculateCartItemPrice(cartItem);
    assertEquals(expectedResult, actualPrice);
  }

  @Test
  void calculateCartTotalPriceTest() {
    var actualPrice = calculatePriceUseCase.calculateCartTotalPrice(cartItems());

    assertEquals(BigDecimal.valueOf(25_200), actualPrice);
  }

  @Test
  void calculateCartItemPriceWithDiscountTest() {
    final CartItem cartItem = getCartItemWithDiscount();

    final BigDecimal actualPrice = calculatePriceUseCase.calculateCartItemPriceWithDiscount(cartItem);

    assertEquals(BigDecimal.valueOf(8999.90).setScale(2, RoundingMode.HALF_DOWN), actualPrice);
  }

  @Test
  void calculateCartItemPriceWithDiscountWhenDiscountIsNullTest() {
    final CartItem cartItem = getCartItem();

    final BigDecimal actualPrice = calculatePriceUseCase.calculateCartItemPriceWithDiscount(cartItem);

    assertNull(actualPrice);
  }

  @Test
  void calculateTotalPriceWithDiscountTest() {
    final CartItem cartItem = getCartItemWithDiscount();

    final BigDecimal actualPrice = calculatePriceUseCase.calculateTotalPriceWithDiscount(List.of(cartItem));

    assertEquals(BigDecimal.valueOf(8999.90).setScale(2, RoundingMode.HALF_DOWN), actualPrice);
  }

  @Test
  void calculateTotalPriceWithDiscountWhenDiscountIsNullTest() {
    final CartItem cartItem = getCartItem();

    final BigDecimal actualPrice = calculatePriceUseCase.calculateTotalPriceWithDiscount(List.of(cartItem));

    assertNull(actualPrice);
  }

  @Test
  void calculateTotalPriceWithDiscountWhenItemOneHasDiscountAndItemTwoHasNoDiscountTest() {
    final CartItem cartItem = getCartItem();
    final CartItem cartItemWithDiscount = getCartItemWithDiscount();

    final BigDecimal actual = calculatePriceUseCase.calculateTotalPriceWithDiscount(List.of(cartItem, cartItemWithDiscount));

    final BigDecimal expected = calculatePriceUseCase.calculateCartItemPrice(cartItem)
        .add(calculatePriceUseCase.calculateCartItemPriceWithDiscount(cartItemWithDiscount));
    assertEquals(expected, actual);
  }
}
