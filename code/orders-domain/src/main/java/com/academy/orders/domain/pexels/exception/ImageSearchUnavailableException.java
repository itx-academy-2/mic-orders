package com.academy.orders.domain.pexels.exception;

public class ImageSearchUnavailableException extends RuntimeException {

  public ImageSearchUnavailableException(String message) {
    super(message);
  }

  public ImageSearchUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
