package com.academy.orders.domain.cart.exception;

import com.academy.orders.domain.common.exception.AlreadyExistsException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CartItemAlreadyExists extends AlreadyExistsException {
  private final UUID productId;

  public CartItemAlreadyExists(UUID productId) {
    super(String.format("Product with id: %s is already present in the cart", productId));
    this.productId = productId;
  }
}
