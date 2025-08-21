package com.academy.orders.domain.product.exception;

public class MissingTranslationsException extends RuntimeException {

  public MissingTranslationsException(String message) {
    super(message);
  }
}
