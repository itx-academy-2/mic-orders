package com.academy.orders.domain.product.exception;

import java.util.UUID;

/**
 * Exception thrown when a product has no available quantity for reservation.
 */
public class ProductOutOfStockException extends RuntimeException {
  public ProductOutOfStockException(UUID productId) {
    super("Product is out of stock: " + productId);
  }
}
