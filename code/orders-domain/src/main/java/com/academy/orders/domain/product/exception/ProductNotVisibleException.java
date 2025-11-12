package com.academy.orders.domain.product.exception;

import java.util.UUID;

/**
 * Exception thrown when a non-visible product is attempted to be reserved.
 */
public class ProductNotVisibleException extends RuntimeException {
  public ProductNotVisibleException(UUID productId) {
    super("Product is not visible: " + productId);
  }
}
