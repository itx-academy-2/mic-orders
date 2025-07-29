package com.academy.orders.domain.wishlist.exception;

/**
 * Exception thrown when an unsupported sort field is requested.
 */
public class UnsupportedSortFieldException extends RuntimeException {
  public UnsupportedSortFieldException(String sortField) {
    super("Unsupported sort field: " + sortField);
  }
}
